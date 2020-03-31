package io.hepp.cov2words.common.dto.originstamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Response DTO for download link.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProofResponseDTO extends OriginStampResponse {

    /**
     * URL of the proof.
     */
    @JsonProperty("download_url")
    private String downloadURL;

    /**
     * Name of the proof.
     */
    @JsonProperty("file_name")
    private String fileName;

    /**
     * Field is not filled by OriginStamp. Ignore.
     */
    @Deprecated
    @JsonProperty("file_size_bytes")
    private long fileSizeBytes;
}
