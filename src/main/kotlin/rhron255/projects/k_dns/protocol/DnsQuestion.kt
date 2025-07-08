package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.utils.readDomainName
import java.nio.ByteBuffer

class DnsQuestion {
    val question: String
    val questionType: RecordType
    val questionClass: RecordClass

    constructor(bytes: ByteBuffer) {
        question = bytes.readDomainName()

        questionType = RecordType.fromBytes(bytes)
        questionClass = RecordClass.fromBytes(bytes)
    }

    override fun toString() = "DnsQuestion{type='$questionType',class='$questionClass',questions=$question}"
}