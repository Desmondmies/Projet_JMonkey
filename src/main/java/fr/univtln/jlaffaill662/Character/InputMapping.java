package fr.univtln.jlaffaill662.Character;

import com.jme3.input.controls.KeyTrigger;

public class InputMapping {
    private String mappingName;
    private KeyTrigger[] mappings;

    public InputMapping(String mappingName, int... keycodes) {
        this.mappingName = mappingName;
        this.mappings = new KeyTrigger[ keycodes.length ];

        for (int i = 0; i < keycodes.length; i++) {
            this.mappings[i] =  new KeyTrigger( keycodes[i] );
        }
    }

    public String getMappingName() { return mappingName; }
    public KeyTrigger[] getMappings() { return mappings; }
}
