/*
 * Copyright (c) 2018 The Gisla Authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gisla.axon.io;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.axonframework.common.ObjectUtils;
import org.axonframework.serialization.ChainingConverter;
import org.axonframework.serialization.Converter;
import org.axonframework.serialization.RevisionResolver;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedObject;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.UnknownSerializedType;

@RequiredArgsConstructor
@Builder
public class GsonSerializer implements Serializer {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final Gson gson;
    private final RevisionResolver revisionResolver;
    private final ClassLoader classLoader;
    private final Converter converter = new ChainingConverter();

//----------------------------------------------------------------------------------------------------------------------
// Getters/Setters
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Converter getConverter() {
        return converter;
    }

//----------------------------------------------------------------------------------------------------------------------
// Serializer Implementation
//----------------------------------------------------------------------------------------------------------------------


    @Override
    public <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation) {
        final String json = gson.toJson(object);
        if (String.class.equals(expectedRepresentation)) {
            return new SimpleSerializedObject<>(
                    expectedRepresentation.cast(json),
                    expectedRepresentation,
                    typeForClass(ObjectUtils.nullSafeTypeOf(object)));
        } else {
            return new SimpleSerializedObject<>(expectedRepresentation.cast(json.getBytes(StandardCharsets.UTF_8)), expectedRepresentation, typeForClass(ObjectUtils.nullSafeTypeOf(object)));
        }
    }

    @Override
    public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
        return String.class.equals(expectedRepresentation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T deserialize(SerializedObject<S> serializedObject) {
        final SerializedObject<String> converted = converter.convert(serializedObject, String.class);
        final Class<T> type = (Class<T>) classForType(serializedObject.getType());
        return gson.fromJson(converted.getData(), type);
    }

    @Override
    public Class<?> classForType(SerializedType type) {
        if (SimpleSerializedType.emptyType().equals(type)) {
            return Void.class;
        }
        try {
            return getClass().getClassLoader().loadClass(type.getName());
        } catch (ClassNotFoundException e) {
            return UnknownSerializedType.class;
        }
    }

    @Override
    public SerializedType typeForClass(Class type) {
        if (type == null || Void.TYPE.equals(type) || Void.class.equals(type)) {
            return SimpleSerializedType.emptyType();
        }
        return new SimpleSerializedType(type.getName(), revisionResolver.revisionOf(type));
    }
}
