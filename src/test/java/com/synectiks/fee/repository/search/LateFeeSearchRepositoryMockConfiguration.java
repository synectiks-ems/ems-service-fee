package com.synectiks.fee.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link LateFeeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class LateFeeSearchRepositoryMockConfiguration {

    @MockBean
    private LateFeeSearchRepository mockLateFeeSearchRepository;

}
