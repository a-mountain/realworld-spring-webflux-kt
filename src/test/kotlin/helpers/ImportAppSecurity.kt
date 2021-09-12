package helpers

import com.realworld.springmongo.security.JwtConfig
import com.realworld.springmongo.security.JwtSigner
import com.realworld.springmongo.security.SecurityConfig
import com.realworld.springmongo.security.TokenFormatter
import org.springframework.context.annotation.Import

@Import(SecurityConfig::class, TokenFormatter::class, JwtSigner::class, JwtConfig::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ImportAppSecurity
