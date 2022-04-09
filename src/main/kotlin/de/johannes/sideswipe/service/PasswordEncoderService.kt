package de.johannes.sideswipe.service

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

class PasswordEncoderService {
    companion object{
        fun String.toSHA256(): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
            return DatatypeConverter.printHexBinary(bytes)
        }
    }
}
