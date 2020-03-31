package io.hepp.cov2words.common.constant.originstamp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the proof type retrieved from OriginStamp.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@AllArgsConstructor
@Getter
public enum ProofType {
    RAW_PROOF(ProofTypeId.RAW_PROOF),
    CERTIFICATE(ProofTypeId.CERTIFICATE);

    private int statusId;

    /**
     * Contains the static ID.
     */
    private static class ProofTypeId {
        private static final int RAW_PROOF = 0;
        private static final int CERTIFICATE = 1;
    }
}
