package com.rcl.msrpg.system.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RpgSystemSettingsTest {

    @Test
    @DisplayName("Deve serializar e desserializar settings padrão")
    void shouldSerializeAndDeserializeDefaults() {
        RpgSystemSettings settings = RpgSystemSettings.defaults();

        String json = settings.toJson();

        RpgSystemSettings restored = RpgSystemSettings.fromJson(json);

        assertNotNull(json);
        assertEquals(settings, restored);
    }

    @Test
    @DisplayName("Deve retornar defaults quando JSON for nulo ou vazio")
    void shouldReturnDefaultsWhenJsonIsBlank() {
        assertEquals(RpgSystemSettings.defaults(), RpgSystemSettings.fromJson(null));
        assertEquals(RpgSystemSettings.defaults(), RpgSystemSettings.fromJson(""));
        assertEquals(RpgSystemSettings.defaults(), RpgSystemSettings.fromJson("   "));
    }

    @Test
    @DisplayName("Deve preencher campos nulos com defaults")
    void shouldFillNullFieldsWithDefaults() {
        String json = """
            {
              "runtime": null,
              "snapshots": null,
              "logs": null,
              "importExport": null
            }
            """;

        RpgSystemSettings settings = RpgSystemSettings.fromJson(json);

        assertEquals(RpgSystemSettings.defaults(), settings);
    }

}
