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
import com.google.gson.JsonPrimitive;
import io.gisla.domain.aggregate.Saga;
import io.gisla.domain.command.CreateSagaCommand;
import io.gisla.domain.value.TransactionDescriptor;
import org.axonframework.serialization.ChainingConverter;
import org.axonframework.serialization.FixedValueRevisionResolver;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SimpleSerializedObject;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.UnknownSerializedType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GsonSerializerTest {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final Gson gson = new Gson();
    private final GsonSerializer serializer = GsonSerializer.builder()
            .gson(gson)
            .revisionResolver(new FixedValueRevisionResolver("1.0"))
            .build();

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void canSerializeTo() {
        assertThat(serializer.canSerializeTo(Saga.class)).isFalse();
        assertThat(serializer.canSerializeTo(String.class)).isTrue();
    }

    @Test
    void classForEmptyTypeIsVoid() {
        assertThat(serializer.classForType(SimpleSerializedType.emptyType())).isEqualTo(Void.class);
    }

    @Test
    void classForType() {
        assertThat(serializer.classForType(new SimpleSerializedType(Saga.class.getName(), "1.0"))).isEqualTo(Saga.class);
    }

    @Test
    void classForUnknownTypeIsUnknownSerializedType() {
        assertThat(serializer.classForType(new SimpleSerializedType("foo", "1.0"))).isEqualTo(UnknownSerializedType.class);
    }

    @Test
    void deserialize() {
        final CreateSagaCommand command = CreateSagaCommand.builder()
                .transaction(TransactionDescriptor.builder()
                        .transactionType("foo")
                        .transactionSpec(new JsonPrimitive("bar"))
                        .build())
                .build();
        final Object deserialized = serializer.deserialize(new SimpleSerializedObject<>(gson.toJson(command), String.class, CreateSagaCommand.class.getName(), "1.0"));
        assertThat(deserialized).isInstanceOf(CreateSagaCommand.class);
        assertThat(deserialized).isEqualTo(command);
    }

    @Test
    void getConverter() {
        assertThat(serializer.getConverter()).isInstanceOf(ChainingConverter.class);
    }

    @Test
    void serialize() {
        final CreateSagaCommand command = CreateSagaCommand.builder()
                .transaction(TransactionDescriptor.builder()
                        .transactionType("foo")
                        .transactionSpec(new JsonPrimitive("bar"))
                        .build())
                .build();
        final SerializedObject<String> serialized = serializer.serialize(command, String.class);
        assertThat(serialized.getContentType()).isEqualTo(String.class);
        assertThat(serialized.getData()).isEqualTo(gson.toJson(command));
    }

    @Test
    void serializeToNonString() {
        final CreateSagaCommand command = CreateSagaCommand.builder()
                .transaction(TransactionDescriptor.builder()
                        .transactionType("foo")
                        .transactionSpec(new JsonPrimitive("bar"))
                        .build())
                .build();
        final SerializedObject<byte[]> serialized = serializer.serialize(command, byte[].class);
        assertThat(serialized.getContentType()).isEqualTo(byte[].class);
        assertThat(serialized.getData()).isEqualTo(gson.toJson(command).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void typeForClass() {
        assertThat(serializer.typeForClass(Saga.class)).isInstanceOf(SimpleSerializedType.class);
        assertThat(serializer.typeForClass(null)).isEqualTo(SimpleSerializedType.emptyType());
        assertThat(serializer.typeForClass(Void.TYPE)).isEqualTo(SimpleSerializedType.emptyType());
        assertThat(serializer.typeForClass(Void.class)).isEqualTo(SimpleSerializedType.emptyType());
    }
}