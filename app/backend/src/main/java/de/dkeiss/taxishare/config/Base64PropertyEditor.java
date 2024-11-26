package de.dkeiss.taxishare.config;

import java.beans.PropertyEditorSupport;
import java.util.Base64;

public class Base64PropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        setValue(new String(Base64.getDecoder().decode(text)));
    }
}