package mission.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "test")
@Builder
public class TestDocument {
    @Id
    private String id;
}
