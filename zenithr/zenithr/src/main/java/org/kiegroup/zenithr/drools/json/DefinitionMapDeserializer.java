/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiegroup.zenithr.drools.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.kiegroup.zenithr.drools.model.DeclaredType;
import org.kiegroup.zenithr.drools.model.FieldType;

public class DefinitionMapDeserializer extends JsonDeserializer<Map<String, FieldType>> {

    @Override
    public Map<String, FieldType> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode nodes = p.getCodec().readTree(p);
        Map<String, FieldType> definition = new HashMap<>();
        for(JsonNode node : nodes) {
            String key = node.get(DeclaredType.FIELD_NAME).asText();
            String type = node.get(DeclaredType.FIELD_TYPE).asText();
            definition.put(key, FieldType.fromString(type));
        }
        return definition;
    }

}
