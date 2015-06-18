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

package org.wso2.carbon.ml.integration.ui.pages.mlui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.carbon.ml.integration.ui.pages.exceptions.InvalidPageException;
import org.wso2.carbon.ml.integration.ui.pages.exceptions.MLUIPageCreationException;

public class NewProjectPage extends MLUIPage {

    private static final Log logger = LogFactory.getLog(NewProjectPage.class);

    /**
     * Creates a NewProject page
     *
     * @param driver    Instance of the web driver
     * @throws org.wso2.carbon.ml.integration.ui.pages.exceptions.MLUIPageCreationException
     */
    public NewProjectPage(WebDriver driver) throws MLUIPageCreationException {
        super(driver);
    }

    /**
     * Create a new project
     * @param projectName
     * @param description
     * @param datasetName
     * @return
     * @throws InvalidPageException
     */
    public MLProjectsPage createNewProject(String projectName, String description, String datasetName) throws InvalidPageException {
        try {

            WebElement projectNameElement = driver.findElement(By.xpath(mlUIElementMapper.getElement("project.name")));
            WebElement descriptionElement = driver.findElement(By.xpath(mlUIElementMapper.getElement("project.description")));
            WebElement datasetNameElement = driver.findElement(By.xpath(mlUIElementMapper.getElement("project.dataset")));

            projectNameElement.sendKeys(projectName);
            descriptionElement.sendKeys(description);
            datasetNameElement.sendKeys(datasetName);

            driver.findElement(By.xpath(mlUIElementMapper.getElement("save.project.button"))).click();
            return new MLProjectsPage(driver);
        } catch (MLUIPageCreationException e) {
            throw new InvalidPageException("Error occurred while creating new project: ", e);
        }
    }
}
