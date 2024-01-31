package com.demo.springsecurity.config.jwt

import com.demo.springsecurity.config.security.AdminUser
import com.demo.springsecurity.config.security.ClaimType
import com.demo.springsecurity.config.security.RequestGrantedAuthority
import com.demo.springsecurity.service.LoginService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.bouncycastle.util.io.pem.PemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Component
class JwtTokenProvider {
    @Autowired private lateinit var loginService: LoginService
    private val _AUTH_HEADER = "Authorization"
    private val accessExpirationInterval = 60 * 60 * 1000L
    private val kf = KeyFactory.getInstance("EC");

    private val privateKey : ECPrivateKey
        get() = (kf.generatePrivate(
            PKCS8EncodedKeySpec(
                PemReader(
                    StringReader(
                        StreamUtils.copyToString(
                            ClassPathResource("jwt/ec-private.pkcs8").inputStream, StandardCharsets.UTF_8
                        )
                    )
                ).readPemObject().content
            )
        ) as ECPrivateKey)

    private val publicKey : ECPublicKey
        get() = (kf.generatePublic(
            X509EncodedKeySpec(
                PemReader(
                    StringReader(
                        StreamUtils.copyToString(
                            ClassPathResource(
                                "jwt/ec-public.pem"
                            ).inputStream, StandardCharsets.UTF_8
                        )
                    )
                ).readPemObject().content
            )
        ) as ECPublicKey)

    fun constructJWK(): String {
        return String.format(
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"%s\",\"y\":\"%s\"}",
            Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.w.affineX.toByteArray()),
            Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.w.affineY.toByteArray())
        )
    }

    fun generateToken(adminUser: AdminUser): String {
        val claims: Claims = Jwts.claims()
        claims[ClaimType.ADMIN_ID.value] = adminUser.getAdminId()
        claims[ClaimType.ADMIN_USER_NO.value] = adminUser.getAdminUserNo()

        val now : Date = Date(System.currentTimeMillis())
        val hour24 : Date = Date(now.time + (1000 * 60 * 60 * 24))

        return Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setSubject("spring-security")
            .setClaims(claims)
            .setIssuer("jhPark")
            .setIssuedAt(now)
            .setExpiration(hour24)
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        if (request.getHeader(_AUTH_HEADER) == null)
            return null

        return request.getHeader(_AUTH_HEADER).replace("(?i)bearer", "").trim();
    }

    fun validateToken(token : String): Boolean {
        val claims: Claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).body

        return EnumSet.allOf(ClaimType::class.java)
            .stream()
            .noneMatch { claimType -> claims.get(claimType.name) == null }
    }

    fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val claims: Claims = Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body

        val adminId: String = claims.get(ClaimType.ADMIN_ID.value, String::class.java)
        val adminUserNo: Long = claims.get(ClaimType.ADMIN_USER_NO.value, Double::class.java).toLong()

        val authorities: List<RequestGrantedAuthority> = loginService.getAuthorityList(adminId)
        val principal = AdminUser(adminId, adminUserNo, authorities)

        return UsernamePasswordAuthenticationToken(principal, null, authorities)
    }
}