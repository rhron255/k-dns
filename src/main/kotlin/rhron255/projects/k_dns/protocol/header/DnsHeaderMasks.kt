package rhron255.projects.k_dns.protocol.header

enum class DnsHeaderMasks(val mask: Short) {
    QUERY_OR_RESPONSE(0b1000_0000_0000_0000.toShort()),
    OPCODE(0b0111_1000_0000_0000),
    AUTHORITATIVE_ANSWER(0b0000_0100_0000_0000),
    TRUNCATION(0b0000_0010_0000_0000),
    RECURSION_DESIRED(0b0000_0001_0000_0000),
    RECURSION_AVAILABLE(0b0000_0000_1000_0000),
    ZERO(0b0000_0000_0111_0000),
    RESPONSE_CODE(0b0000_0000_0000_1111),
}

enum class DnsOpcode {
    STANDARD_QUERY,
    INVERSE_QUERY,
    SERVER_STATUS_REQUEST,
    RESERVED_3,
    RESERVED_4,
    RESERVED_5,
    RESERVED_6,
    RESERVED_7,
    RESERVED_8,
    RESERVED_9,
    RESERVED_10,
    RESERVED_11,
    RESERVED_12,
    RESERVED_13,
    RESERVED_14,
    RESERVED_15;

    companion object {
        fun <T : Number> from(code: T) =
            entries.find { it.ordinal == code.toInt() }
                ?: throw IllegalStateException("$code is not a valid DNS header opcode")

    }
}

enum class DnsResponseCode {
    NO_ERROR,
    FORMAT_ERROR,
    SERVER_FAILURE,

    /** Requested domain does not exist */
    NAME_ERROR,

    /** When an optional feature isn't supported */
    NOT_IMPLEMENTED,
    REFUSED,
    RESERVED_6,
    RESERVED_7,
    RESERVED_8,
    RESERVED_9,
    RESERVED_10,
    RESERVED_11,
    RESERVED_12,
    RESERVED_13,
    RESERVED_14,
    RESERVED_15,
    RESERVED_16;

    companion object {
        fun <T : Number> from(code: T) =
            DnsResponseCode.entries.find { it.ordinal == code.toInt() }
                ?: throw IllegalStateException("$code is not a valid DNS response")
    }
}