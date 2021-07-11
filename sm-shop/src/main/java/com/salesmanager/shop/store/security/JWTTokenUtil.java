package com.salesmanager.shop.store.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.salesmanager.shop.store.security.user.JWTUser;
import com.salesmanager.shop.utils.DateUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Used for managing token based authentication for customer and user
 * @author c.samson
 *
 */
@Component
public class JWTTokenUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	    static final int GRACE_PERIOD = 200;
	
	
	
	 	static final String CLAIM_KEY_USERNAME = "sub";
	    static final String CLAIM_KEY_AUDIENCE = "aud";
	    static final String CLAIM_KEY_CREATED = "iat";

	    static final String AUDIENCE_UNKNOWN = "unknown";
	    static final String AUDIENCE_API = "api";
	    static final String AUDIENCE_WEB = "web";
	    static final String AUDIENCE_MOBILE = "mobile";
	    static final String AUDIENCE_TABLET = "tablet";


	    @Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.expiration}")
	    private Long expiration;

	    public String getUsernameFromToken(String token) {
									System.out.println("$#15294#"); return getClaimFromToken(token, Claims::getSubject);
	    }

	    public Date getIssuedAtDateFromToken(String token) {
									System.out.println("$#15295#"); return getClaimFromToken(token, Claims::getIssuedAt);
	    }

	    public Date getExpirationDateFromToken(String token) {
									System.out.println("$#15296#"); return getClaimFromToken(token, Claims::getExpiration);
	    }

	    public String getAudienceFromToken(String token) {
									System.out.println("$#15297#"); return getClaimFromToken(token, Claims::getAudience);
	    }

	    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = getAllClaimsFromToken(token);
									System.out.println("$#15298#"); return claimsResolver.apply(claims);
	    }

	    private Claims getAllClaimsFromToken(String token) {
									System.out.println("$#15299#"); return Jwts.parser()
	                .setSigningKey(secret)
	                .parseClaimsJws(token)
	                .getBody();
	    }

	    private Boolean isTokenExpired(String token) {
	        final Date expiration = getExpirationDateFromToken(token);
									System.out.println("$#15301#"); System.out.println("$#15300#"); return expiration.before(DateUtil.getDate());
	    }
	    
	    private Boolean isTokenExpiredWithGrace(String token) {
	            Date expiration = getExpirationDateFromToken(token);
	            expiration = addSeconds(expiration,GRACE_PERIOD);
													System.out.println("$#15303#"); System.out.println("$#15302#"); return expiration.before(DateUtil.getDate());
	    }

	    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
									System.out.println("$#15306#"); System.out.println("$#15305#"); System.out.println("$#15304#"); return (lastPasswordReset != null && created.before(lastPasswordReset));
	    }
	    
	    private Boolean isCreatedBeforeLastPasswordResetWithGrace(Date created, Date lastPasswordReset) {
									System.out.println("$#15310#"); System.out.println("$#15309#"); System.out.println("$#15308#"); return (lastPasswordReset != null && created.before(addSeconds(lastPasswordReset,GRACE_PERIOD)));
	    }
	    
	    private Date addSeconds(Date date, Integer seconds) {
	      Calendar cal = Calendar.getInstance();
							System.out.println("$#15312#"); cal.setTime(date);
							System.out.println("$#15313#"); cal.add(Calendar.SECOND, seconds);
							System.out.println("$#15314#"); return cal.getTime();
	    }

	    private String generateAudience() {
									System.out.println("$#15315#"); return AUDIENCE_API;
	    }

	    private Boolean ignoreTokenExpiration(String token) {
	        String audience = getAudienceFromToken(token);
									System.out.println("$#15318#"); System.out.println("$#15317#"); System.out.println("$#15316#"); return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
	    }

	    public String generateToken(UserDetails userDetails) {
	        Map<String, Object> claims = new HashMap<>();
									System.out.println("$#15320#"); return doGenerateToken(claims, userDetails.getUsername(), generateAudience());
	    }

	    private String doGenerateToken(Map<String, Object> claims, String subject, String audience) {
	        final Date createdDate = DateUtil.getDate();
	        final Date expirationDate = calculateExpirationDate(createdDate);

									System.out.println("$#15321#"); System.out.println("doGenerateToken " + createdDate);

									System.out.println("$#15322#"); return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(subject)
	                .setAudience(audience)
	                .setIssuedAt(createdDate)
	                .setExpiration(expirationDate)
	                .signWith(SignatureAlgorithm.HS512, secret)
	                .compact();
	    }
	    
        public Boolean canTokenBeRefreshedWithGrace(String token, Date lastPasswordReset) {
          final Date created = getIssuedAtDateFromToken(token);
          boolean t = isCreatedBeforeLastPasswordResetWithGrace(created, lastPasswordReset);
          boolean u = isTokenExpiredWithGrace(token);
          boolean v =  ignoreTokenExpiration(token);
										System.out.println("$#15323#"); System.out.println(t + " " +  u + " " + v);
										System.out.println("$#15325#"); System.out.println(!isCreatedBeforeLastPasswordResetWithGrace(created, lastPasswordReset)
                  && (!isTokenExpiredWithGrace(token) || ignoreTokenExpiration(token)));
          //return !isCreatedBeforeLastPasswordResetWithGrace(created, lastPasswordReset)
          //        && (!isTokenExpired(token) || ignoreTokenExpiration(token));
										System.out.println("$#15328#"); return true;
        }	    

	    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
	        final Date created = getIssuedAtDateFromToken(token);
									System.out.println("$#15330#"); System.out.println("$#15329#"); return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
	                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
	    }

	    public String refreshToken(String token) {
	        final Date createdDate = DateUtil.getDate();
	        final Date expirationDate = calculateExpirationDate(createdDate);

	        final Claims claims = getAllClaimsFromToken(token);
	        claims.setIssuedAt(createdDate);
	        claims.setExpiration(expirationDate);

									System.out.println("$#15334#"); return Jwts.builder()
	                .setClaims(claims)
	                .signWith(SignatureAlgorithm.HS512, secret)
	                .compact();
	    }

	    public Boolean validateToken(String token, UserDetails userDetails) {
	        JWTUser user = (JWTUser) userDetails;
	        final String username = getUsernameFromToken(token);
	        final Date created = getIssuedAtDateFromToken(token);
	        //final Date expiration = getExpirationDateFromToken(token);
	        
	        boolean usernameEquals = username.equals(user.getUsername());
	        boolean isTokenExpired = isTokenExpired(token);
	        boolean isTokenCreatedBeforeLastPasswordReset = isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate());
	        
									System.out.println("$#15336#"); System.out.println("$#15335#"); return (

	        		usernameEquals && !isTokenExpired && !isTokenCreatedBeforeLastPasswordReset
	        );
	    }

	    private Date calculateExpirationDate(Date createdDate) {
									System.out.println("$#15342#"); System.out.println("$#15341#"); System.out.println("$#15340#"); return new Date(createdDate.getTime() + expiration * 1000);
	    }

}
