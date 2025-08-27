package com.itb.inf2fm.projetoback.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Base64;

/**
 * Deserializador customizado para converter strings Base64 em byte arrays
 * Trata casos de string vazia ou null
 */
public class Base64ToByteArrayDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException e) {
            // Se a string não for um Base64 válido, retorna null
            return null;
        }
    }
}
