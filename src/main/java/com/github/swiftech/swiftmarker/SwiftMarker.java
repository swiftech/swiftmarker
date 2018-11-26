/*
 * Copyright 2018 Yuxing Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

/**
 * SwiftMarker is a lightweight template engine.
 *
 * @author swiftech
 **/
public class SwiftMarker {

    private TemplateEngine templateEngine = new TemplateEngine();

    /**
     * Prepare the template before rendering.
     *
     * @param template
     * @param config
     * @return
     */
    public SwiftMarker prepare(String template, Config config) {
        if (StringUtils.isBlank(template)) {
            throw new RuntimeException("Template required");
        }
        templateEngine.setTemplate(template);
        if (config != null) {
            templateEngine.setConfig(config);
        }
        return this;
    }

    /**
     * Prepare the template before rendering.
     *
     * @param template
     * @return
     */
    public SwiftMarker prepare(String template) {
        return prepare(template, null);
    }

    /**
     * Render the template by data model object
     *
     * @param dataModel
     * @return
     */
    public String render(Object dataModel) {
        return this.templateEngine.eachLine(new DefaultDataModelHandler<>(dataModel));
    }
}
