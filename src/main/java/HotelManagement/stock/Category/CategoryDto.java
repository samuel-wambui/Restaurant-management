package HotelManagement.stock.Category;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class CategoryDto {
    private String categoryName;
    private Set<Long> departmentIds;
}
