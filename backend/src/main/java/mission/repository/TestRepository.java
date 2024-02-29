package mission.repository;

import mission.document.TestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<TestDocument, String> {
}
