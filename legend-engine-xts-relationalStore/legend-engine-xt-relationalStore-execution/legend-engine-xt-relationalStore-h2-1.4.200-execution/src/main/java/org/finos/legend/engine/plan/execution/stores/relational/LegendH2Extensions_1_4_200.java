//  Copyright 2023 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.finos.legend.engine.plan.execution.stores.relational;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueFloat;
import org.h2.value.ValueInt;
import org.h2.value.ValueLong;
import org.h2.value.ValueNull;
import org.h2.value.ValueString;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class LegendH2Extensions_1_4_200
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.getNewStandardObjectMapper();

    public static Value legend_h2_extension_json_navigate(Value json, Value property, Value arrayIndex) throws Exception
    {
        if (json == ValueNull.INSTANCE)
        {
            return ValueNull.INSTANCE;
        }

        Object res;
        if (arrayIndex == ValueNull.INSTANCE)
        {
            res = OBJECT_MAPPER.readValue(json.getString(), HashMap.class).get(property.getString());
        }
        else
        {
            ArrayList<?> list = OBJECT_MAPPER.readValue(json.getString(), ArrayList.class);
            res = arrayIndex.getInt() < list.size() ? list.get(arrayIndex.getInt()) : null;
        }

        if (res == null)
        {
            return ValueNull.INSTANCE;
        }
        else if (res instanceof Map || res instanceof List)
        {
            return ValueString.get(OBJECT_MAPPER.writeValueAsString(res));
        }
        else if (res instanceof String)
        {
            return ValueString.get((String) res);
        }
        else if (res instanceof Boolean)
        {
            return ValueBoolean.get((boolean) res);
        }
        else if (res instanceof Double)
        {
            return ValueDouble.get((double) res);
        }
        else if (res instanceof Float)
        {
            return ValueFloat.get((float) res);
        }
        else if (res instanceof Integer)
        {
            return ValueInt.get((int) res);
        }
        else if (res instanceof Long)
        {
            return ValueLong.get((long) res);
        }

        throw new RuntimeException("Unsupported value in H2 extension function");

    }

    public static Value legend_h2_extension_json_parse(Value json) throws Exception
    {
        if (json == ValueNull.INSTANCE)
        {
            return ValueNull.INSTANCE;
        }

        // Ensure validity of JSON
        Object res;
        try
        {
            res = OBJECT_MAPPER.readValue(json.getString(), HashMap.class);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("Unable to parse json as a Map. Content: '" + json.getString() + "'. Error: '" + e.getMessage() + "'");
        }

        return ValueString.get(OBJECT_MAPPER.writeValueAsString(res));
    }

    public static String legend_h2_extension_base64_decode(String string)
    {
        return string == null ? null : new String(Base64.decodeBase64(string));
    }

    public static String legend_h2_extension_base64_encode(String string)
    {
        return string == null ? null : Base64.encodeBase64URLSafeString(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String legend_h2_extension_reverse_string(String string)
    {
        return string == null ? null : new StringBuilder(string).reverse().toString();
    }

    public static String legend_h2_extension_hash_md5(String string)
    {
        return string == null ? null : DigestUtils.md5Hex(string);
    }

    public static String legend_h2_extension_hash_sha1(String string)
    {
        return string == null ? null : DigestUtils.sha1Hex(string);
    }

    public static String legend_h2_extension_split_part(String string, String token, Integer part)
    {
        if (part < 1) 
        {
            throw new IllegalArgumentException("Split part must be greater than zero");
        }

        if (string == null) 
        {
            return null;
        }

        String[] parts = StringUtils.split(string, token);
        int readjustedPart = part - 1;

        return parts.length > readjustedPart ? parts[readjustedPart] : null;
    }
}
