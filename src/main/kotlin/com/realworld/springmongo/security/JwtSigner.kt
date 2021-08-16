package com.realworld.springmongo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtSigner {

    private val keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)
    private val jwtParser = Jwts.parserBuilder()
        .setSigningKey(keyPair.public)
        .build()

    fun validate(jwt: String): Jws<Claims> = jwtParser.parseClaimsJws(jwt)

    fun generateToken(userId: String): String = Jwts.builder()
        .signWith(keyPair.private, SignatureAlgorithm.RS256)
        .setSubject(userId)
        .setExpiration(expirationDate())
        .setIssuer("identity")
        .compact()

    private fun expirationDate(): Date {
        val expirationDate = System.currentTimeMillis() + sessionTime()
        return Date(expirationDate)
    }

    private fun sessionTime(): Long = 86400 * 1000L
}