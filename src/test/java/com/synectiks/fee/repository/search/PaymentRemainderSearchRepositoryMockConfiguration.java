package com.synectiks.fee.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PaymentRemainderSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PaymentRemainderSearchRepositoryMockConfiguration {

    @MockBean
    private PaymentRemainderSearchRepository mockPaymentRemainderSearchRepository;

}
