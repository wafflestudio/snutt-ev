package com.wafflestudio.snuttev.core.common.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class PageUtils {
    companion object {
        private const val SECRET_KEY = "cc42f94d-f47d-40cf-9968-87c5337f"

        fun generateCursor(cursor: Any?): String? = cursor?.let {
            val json = jacksonObjectMapper().writeValueAsString(it)
            val encrypted = getCipher(Cipher.ENCRYPT_MODE).doFinal(json.toByteArray())
            return Base64.getUrlEncoder().encodeToString(encrypted)
        }

        inline fun <reified T> getCursor(cursorString: String?): T? {
            if (cursorString.isNullOrEmpty()) {
                return null
            }
            val base64Decoded = Base64.getUrlDecoder().decode(cursorString)
            val decrypted = getCipher(Cipher.DECRYPT_MODE).doFinal(base64Decoded)
            return jacksonObjectMapper().readValue<T>(decrypted)
        }

        fun getCipher(mode: Int): Cipher {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            cipher.init(mode, secretKeySpec)
            return cipher
        }
    }
}
