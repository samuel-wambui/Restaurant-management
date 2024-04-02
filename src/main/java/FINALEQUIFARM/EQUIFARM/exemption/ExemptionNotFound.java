package FINALEQUIFARM.EQUIFARM.exemption;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExemptionNotFound extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object fieldValue;



    public ExemptionNotFound( String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }
}

