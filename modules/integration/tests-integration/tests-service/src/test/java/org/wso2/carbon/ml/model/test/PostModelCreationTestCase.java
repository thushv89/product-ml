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

package org.wso2.carbon.ml.model.test;

import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.core.Response;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.MLTestUtils;
import org.wso2.carbon.ml.integration.common.utils.MLBaseTest;
import org.wso2.carbon.ml.integration.common.utils.MLHttpClient;
import org.wso2.carbon.ml.integration.common.utils.MLIntegrationTestConstants;
import org.wso2.carbon.ml.integration.common.utils.exception.MLHttpClientException;

/**
 * Contains test cases related to retrieving models
 */
@Test(groups="getModels")
public class PostModelCreationTestCase extends MLBaseTest {

    private MLHttpClient mlHttpclient;
    private int projectId;
    private int analysisId;
    private int versionSetId;
    private int modelId;
    private String modelName;
    
    @BeforeClass(alwaysRun = true)
    public void initTest() throws Exception {
        super.init();
        mlHttpclient = getMLHttpClient();
        String version = "1.0";
        int datasetId = createDataset(MLIntegrationTestConstants.DATASET_NAME_DIABETES, version,
                MLIntegrationTestConstants.DIABETES_DATASET_SAMPLE);
        versionSetId = getVersionSetId(datasetId, version);
        isDatasetProcessed(versionSetId, MLIntegrationTestConstants.THREAD_SLEEP_TIME_LARGE, 1000);
        projectId = createProject(MLIntegrationTestConstants.PROJECT_NAME_DIABETES,
                MLIntegrationTestConstants.DATASET_NAME_DIABETES);
        analysisId = createAnalysis(MLIntegrationTestConstants.ANALYSIS_NAME, projectId);
        buildModelWithLearningAlgorithm("LOGISTIC_REGRESSION", MLIntegrationTestConstants.CLASSIFICATION);
    }

    /**
     * Get model from name
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(priority=1, description = "retrieve a model")
    public void testGetModel() throws MLHttpClientException, IOException, JSONException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/models/" + modelName);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        JSONObject responseJson = new JSONObject(bufferedReader.readLine());
        bufferedReader.close();
        response.close();
        //Check whether the correct model is retrieved
        assertEquals("Incorrect model retrieved.", modelId,responseJson.getInt("id"));
    }
    
    /**
     * @throws MLHttpClientException
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test(priority=1, description = "retrieve models of a project")
    public void testGetCompletedModelsOfProject() throws MLHttpClientException, IOException, JSONException, InterruptedException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/projects/" + projectId + "/models");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = bufferedReader.readLine();
        JSONArray responseJson = new JSONArray(line);
        JSONObject modelJson = (JSONObject) responseJson.get(0);
        bufferedReader.close();
        response.close();
        // Check whether the correct model is retrieved
        assertEquals("Incorrect model retrieved.", modelId, modelJson.getInt("id"));
    }
    
    /**
     * @throws MLHttpClientException
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test(priority=1, description = "retrieve models of an analysis")
    public void testGetModelsOfAnalysis() throws MLHttpClientException, IOException, JSONException, InterruptedException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/analyses/" + analysisId + "/models");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = bufferedReader.readLine();
        JSONArray responseJson = new JSONArray(line);
        JSONObject modelJson = (JSONObject) responseJson.get(0);
        bufferedReader.close();
        response.close();
        // Check whether the correct model is retrieved
        assertEquals("Incorrect model retrieved.", modelId, modelJson.getInt("id"));
    }
    
    /**
     * @throws MLHttpClientException
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test(priority=1, description = "retrieve all models")
    public void testGetAllModels() throws MLHttpClientException, IOException, JSONException, InterruptedException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/models/");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = bufferedReader.readLine();
        JSONArray responseJson = new JSONArray(line);
        JSONObject modelJson = (JSONObject) responseJson.get(0);
        bufferedReader.close();
        response.close();
        // Check whether the correct model is retrieved
        assertEquals("Incorrect model retrieved.", modelId, modelJson.getInt("id"));
    }
    
    /**
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(priority=2, description = "retrieve model summary")
    public void testGetModelSummary() throws MLHttpClientException, IOException, JSONException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/models/" + modelId+"/summary");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
    }
    
    /**
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(priority=3, description = "Publish a Model")
    public void testPublishModel() throws MLHttpClientException, IOException {
        CloseableHttpResponse response = mlHttpclient.doHttpPost("/api/models/"+modelId+"/publish", null);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(priority=3, description = "Publish a non-existing Model")
    public void testPublishNonExistingModel() throws MLHttpClientException, IOException {
        CloseableHttpResponse response = mlHttpclient.doHttpPost("/api/models/"+999+"/publish", null);
        assertEquals("Unexpected response received", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(priority=3, description = "Build a non-existing Model")
    public void testBuildModelWithInvalidId() throws MLHttpClientException, IOException {
        CloseableHttpResponse response = mlHttpclient.doHttpPost("/api/models/"+999, null);
        assertEquals("Unexpected response received", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * Test deleting a model by name.
     * 
     * @throws MLHttpClientException 
     * @throws IOException
     */
    @Test(priority=10, description = "Delete a model")
    public void testDeleteModel() throws MLHttpClientException, IOException {
        CloseableHttpResponse response = mlHttpclient.doHttpDelete("/api/models/" + modelId);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * A test case for building a model with the given learning algorithm
     * 
     * @param algorithmName Name of the learning algorithm
     * @param algorithmType Type of the learning algorithm
     * @throws MLHttpClientException
     * @throws IOException
     * @throws JSONException
     * @throws InterruptedException
     */
    private void buildModelWithLearningAlgorithm(String algorithmName, String algorithmType)
            throws MLHttpClientException, IOException, JSONException, InterruptedException {
        modelName = MLTestUtils.createModelWithConfigurations(algorithmName, algorithmType,
                MLIntegrationTestConstants.RESPONSE_ATTRIBUTE_DIABETES, MLIntegrationTestConstants.TRAIN_DATA_FRACTION,
                projectId, versionSetId, analysisId, mlHttpclient);
        modelId = mlHttpclient.getModelId(modelName);
        CloseableHttpResponse response = mlHttpclient.doHttpPost("/api/models/" + modelId, null);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
        // Waiting for model building to end
        boolean status = MLTestUtils.checkModelStatus(modelName, mlHttpclient,
                MLIntegrationTestConstants.THREAD_SLEEP_TIME_LARGE, 1000);
        // Checks whether model building completed successfully
        assertEquals("Model building did not complete successfully", true, status);
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() throws MLHttpClientException {
        super.destroy();
    }
}