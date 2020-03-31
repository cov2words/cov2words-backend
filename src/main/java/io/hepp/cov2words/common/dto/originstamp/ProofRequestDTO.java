package io.hepp.cov2words.common.dto.originstamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Request object that contains the data for requesting a proof.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProofRequestDTO extends OriginStampRequest {

    /**
     * Currency ID for which the proof is requested.
     * 0: Bitcoin
     * 1: Ethereum
     * 2: AION
     * 100: Suedkurier
     */
    @JsonProperty("currency")
    private int currencyId;

    /**
     * SHA-256 of the original file.
     */
    @JsonProperty("hash_string")
    private String hashString;

    /**
     * Proof type flag:
     * 0: Raw proof (Merkle Tree as XML)
     * 1: PDF certificate
     */
    @JsonProperty("proof_type")
    private int proofType;
}
