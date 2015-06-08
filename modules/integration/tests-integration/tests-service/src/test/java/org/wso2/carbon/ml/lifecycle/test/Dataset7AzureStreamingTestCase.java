/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ml.lifecycle.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONException;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.MLTestUtils;
import org.wso2.carbon.ml.integration.common.utils.MLBaseTest;
import org.wso2.carbon.ml.integration.common.utils.MLHttpClient;
import org.wso2.carbon.ml.integration.common.utils.MLIntegrationTestConstants;
import org.wso2.carbon.ml.integration.common.utils.exception.MLHttpClientException;
import org.wso2.carbon.ml.integration.common.utils.exception.MLIntegrationBaseTestException;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * This class contains the entire ML life-cycle for Azure Streaming dataset
 */
@Test(groups="AzureStreamingDataset")
public class Dataset7AzureStreamingTestCase extends MLBaseTest {

    private MLHttpClient mlHttpclient;
    private static String modelName;
    private static int modelId;
    private CloseableHttpResponse response;
    private int datasetId;

    @BeforeClass(alwaysRun = true)
    public void initTest() throws MLIntegrationBaseTestException {
        super.init();
        mlHttpclient = new MLHttpClient(instance, userInfo);
    }

    /**
     * Creates dataset for Clustering - AzureStreaming
     * @throws MLHttpClientException
     * @throws IOException
     */
    @Test(description = "Create a dataset of AzureStreaming data from a CSV file", groups="createDatasetAzureStreaming")
    public void testCreateDatasetAzureStreaming() throws MLHttpClientException, IOException, JSONException {
        response = mlHttpclient.uploadDatasetFromCSV(MLIntegrationTestConstants.DATASET_NAME_AZURE_STREAMING,
                "1.0", MLIntegrationTestConstants.AZURE_STREAMING_DATASET_SAMPLE);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        datasetId = MLTestUtils.getId(response);
        response.close();
    }

    /**
     * Creates a test case for creating a project for AzureStreaming data set
     * @throws MLHttpClientException
     * @throws IOException
     */
    @Test(description = "Create a project for AzureStreaming dataset",
            groups="createProjectAzureStreaming", dependsOnGroups="createDatasetAzureStreaming")
    public void testCreateProjectAzureStreaming() throws MLHttpClientException, IOException {
        response = mlHttpclient.createProject(MLIntegrationTestConstants.PROJECT_NAME_AZURE_STREAMING,
                MLIntegrationTestConstants.DATASET_NAME_AZURE_STREAMING);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }

    /**
     * A test case for building a model with the given learning algorithm
     * @param algorithmName             Name of the learning algorithm
     * @param algorithmType             Type of the learning algorithm
     * @throws MLHttpClientException
     * @throws IOException
     * @throws JSONException
     * @throws InterruptedException
     */
    private void buildModelWithLearningAlgorithm(String algorithmName, String algorithmType) throws MLHttpClientException,
            IOException, JSONException, InterruptedException {
        modelName= MLTestUtils.setConfiguration(algorithmName, algorithmType,
                MLIntegrationTestConstants.RESPONSE_ATTRIBUTE_AZURE_STREAMING, MLIntegrationTestConstants.TRAIN_DATA_FRACTION,
                mlHttpclient.getProjectId(MLIntegrationTestConstants.PROJECT_NAME_AZURE_STREAMING),
                datasetId, mlHttpclient);
        modelId = mlHttpclient.getModelId(modelName);
        response = mlHttpclient.doHttpPost("/api/models/" + modelId, null);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
        // Waiting for model building to end
        Thread.sleep(MLIntegrationTestConstants.THREAD_SLEEP_TIME_MEDIUM);
        // Checks whether model building completed successfully is true
        assertEquals("Model building did not complete successfully", true, MLTestUtils.checkModelStatus(modelName, mlHttpclient));
    }

    /**
     * Creates a test case for creating an analysis, building a K-Means clustering
     * @throws MLHttpClientException
     * @throws IOException
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test(description = "Build a K-means model",
            groups="createKMeansAzureStreaming", dependsOnGroups="createProjectAzureStreaming")
    public void testBuildKMeansModel() throws MLHttpClientException, IOException, JSONException, InterruptedException {
        // Check whether the project is created otherwise skip
        response = mlHttpclient.doHttpGet("/api/projects/" + MLIntegrationTestConstants
                .PROJECT_NAME_AZURE_STREAMING);
        if (Response.Status.OK.getStatusCode() != response.getStatusLine().getStatusCode()) {
            throw new SkipException("Skipping tests because a project is not available");
        }
        buildModelWithLearningAlgorithm("K_MEANS", MLIntegrationTestConstants.CLUSTERING);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws InterruptedException, MLHttpClientException {
        mlHttpclient.doHttpDelete("/api/projects/" + MLIntegrationTestConstants.PROJECT_NAME_AZURE_STREAMING);
        mlHttpclient.doHttpDelete("/api/datasets/" + datasetId);
    }
}
