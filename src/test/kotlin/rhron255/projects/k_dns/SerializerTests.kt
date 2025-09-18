package rhron255.projects.k_dns

import rhron255.projects.k_dns.protocol.DnsQuestion
import rhron255.projects.k_dns.protocol.RecordClass
import rhron255.projects.k_dns.protocol.RecordType
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class SerializerTests {
    val YOUTUBE_DNS_RESPONSE =
        "0000818000010007000000000377777707796f757475626503636f6d0000010001c00c000500010000006300160a796f75747562652d7569016c06676f6f676c65c018c02d000100010000006300048efa4b2ec02d000100010000006300048efa4bcec02d000100010000006300048efa4baec02d000100010000006300048efa4b8ec02d000100010000006300048efa4b6ec02d000100010000006300048efa4b4e".hexToByteArray()
    val DNS_QUERY = "0377777707796f757475626503636f6d0000010001".hexToByteArray()

    @Test
    fun `parse query`() {
        with(DnsQuestion(ByteBuffer.wrap(DNS_QUERY))) {
            assertEquals(question, "www.youtube.com")
            assertEquals(questionType, RecordType.A)
            assertEquals(questionClass, RecordClass.IN)
            val bytes = toBytes()
            val sizeMismatch = bytes.size != DNS_QUERY.size
            if (!bytes.contentEquals(DNS_QUERY)) {
                (bytes zip DNS_QUERY).forEachIndexed { i, (new, original) ->
                    if (new != original) {
                        throw Exception(
                            (if (sizeMismatch) "Size mismatch - original: ${DNS_QUERY.size} new: ${bytes.size}\n" else "") +
                                    "Byte mismatch at $i, original was: ${original.toHexString()}, but found: ${new.toHexString()}"
                        )
                    }
                }
            }
        }
    }

}
