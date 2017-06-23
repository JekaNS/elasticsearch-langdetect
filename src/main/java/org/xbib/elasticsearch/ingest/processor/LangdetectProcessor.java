/*
 * Copyright [2017] [Ievgenii Kovtun]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.xbib.elasticsearch.ingest.processor;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;
import org.xbib.elasticsearch.common.langdetect.LangdetectService;
import org.xbib.elasticsearch.common.langdetect.Language;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class LangdetectProcessor extends AbstractProcessor {

    public static final String TYPE = "langdetect";

    private final String field;
    private final String targetField;
    private final LangdetectService service;

    public LangdetectProcessor(String tag, String field, String targetField) throws IOException {
        super(tag);
        this.field = field;
        this.targetField = targetField;
        Settings.Builder settingsBuilder = Settings.builder();
        this.service = new LangdetectService(settingsBuilder.build());
    }

    @Override
    public void execute(IngestDocument ingestDocument) throws Exception {
        String content = ingestDocument.getFieldValue(field, String.class);
        List<Language> languages = this.service.detectAll(content);
        String bestLanguage = "";
        if(!languages.isEmpty()) {
            bestLanguage = languages.get(0).getLanguage();
        }
        ingestDocument.setFieldValue(targetField, bestLanguage);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {

        @Override
        public LangdetectProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config) 
            throws Exception {
            String field = readStringProperty(TYPE, tag, config, "field");
            String targetField = readStringProperty(TYPE, tag, config, "target_field");

            return new LangdetectProcessor(tag, field, targetField);
        }
    }
}
