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

package org.wso2.carbon.ml.analysis.test;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.integration.common.utils.MLBaseTest;
import org.wso2.carbon.ml.integration.common.utils.MLHttpClient;
import org.wso2.carbon.ml.integration.common.utils.MLIntegrationTestConstants;
import org.wso2.carbon.ml.integration.common.utils.exception.MLHttpClientException;

/**
 * Class contains test cases related to retrieving analyses
 */
@Test(groups="getAnalyses")
public class GetAnalysesTestCase extends MLBaseTest {

    private MLHttpClient mlHttpclient;
    private int projectId;
    
    @BeforeClass(alwaysRun = true)
    public void initTest() throws Exception {
        super.init();
        mlHttpclient = getMLHttpClient();
        createDataset(MLIntegrationTestConstants.DATASET_NAME_DIABETES, "1.0",
                MLIntegrationTestConstants.DIABETES_DATASET_SAMPLE);
        isDatasetProcessed(getVersionSetIds().get(0), MLIntegrationTestConstants.THREAD_SLEEP_TIME_LARGE, 1000);
        projectId = createProject(MLIntegrationTestConstants.PROJECT_NAME_DIABETES,
                MLIntegrationTestConstants.DATASET_NAME_DIABETES);
        createAnalysis(MLIntegrationTestConstants.ANALYSIS_NAME, projectId);
        createAnalysis(MLIntegrationTestConstants.ANALYSIS_NAME_2, projectId);
    }

    /**
     * Test retrieving all analyzes.
     * 
     * @throws MLHttpClientException 
     */
    @Test(description = "Get all analyses")
    public void testGetAllAnalyzes() throws MLHttpClientException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/analyses/");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
    }
    
    /**
     * Test retrieving all analyzes from project API.
     * 
     * @throws MLHttpClientException 
     */
    @Test(description = "Get all analyses")
    public void testGetAllAnalyzesFromProject() throws MLHttpClientException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/projects/analyses");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
    }
    
    /**
     * Test retrieving all analyzes.
     * 
     * @throws MLHttpClientException 
     */
    @Test(description = "Get all analyses")
    public void testGetAllAnalyzesOfProject() throws MLHttpClientException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/projects/"+projectId+"/analyses");
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
    }
    
    /**
     * Test retrieving an analysis by name.
     * 
     * @throws MLHttpClientException 
     * @throws IOException 
     */
    @Test(description = "Retrieve an analysis by name")
    public void testGetAnalysis() throws MLHttpClientException, IOException  {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/projects/"+projectId+ "/analyses/" + MLIntegrationTestConstants
                .ANALYSIS_NAME);
        assertEquals("Unexpected response received", Response.Status.OK.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * Test retrieving a non-existing analysis.
     * 
     * @throws MLHttpClientException 
     * @throws IOException
     */
    @Test(description = "Retrieve a non-existing analysis")
    public void testGetNonExistingAnalysis() throws MLHttpClientException, IOException {
        CloseableHttpResponse response = mlHttpclient.doHttpGet("/api/projects/"+projectId+ "/analyses/" + "nonExistingAnalysisName");
        assertEquals("Unexpected response received", Response.Status.NOT_FOUND.getStatusCode(), response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() throws MLHttpClientException {
        super.destroy();
    }
}