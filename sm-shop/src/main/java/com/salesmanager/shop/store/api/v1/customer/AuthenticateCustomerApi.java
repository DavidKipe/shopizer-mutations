package com.salesmanager.shop.store.api.v1.customer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.Validate;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.security.AuthenticationRequest;
import com.salesmanager.shop.store.security.AuthenticationResponse;
import com.salesmanager.shop.store.security.JWTTokenUtil;
import com.salesmanager.shop.store.security.PasswordRequest;
import com.salesmanager.shop.store.security.user.JWTUser;
import com.salesmanager.shop.utils.LanguageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController
@RequestMapping("/api/v1")
@Api(tags = {"Customer authentication resource (Customer Authentication Api)"})
@SwaggerDefinition(tags = {
    @Tag(name = "Customer authentication resource", description = "Authenticates customer, register customer and reset customer password")
})
public class AuthenticateCustomerApi {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateCustomerApi.class);

    @Value("${authToken.header}")
    private String tokenHeader;

    @Inject
    private AuthenticationManager jwtCustomerAuthenticationManager;

    @Inject
    private JWTTokenUtil jwtTokenUtil;

    @Inject
    private UserDetailsService jwtCustomerDetailsService;
    
    @Inject
    private CustomerFacade customerFacade;
    
    @Inject
    private StoreFacade storeFacade;
    
    @Inject
    private LanguageUtils languageUtils;
    
    /**
     * Create new customer for a given MerchantStore, then authenticate that customer
     */
    @RequestMapping( value={"/customer/register"}, method=RequestMethod.POST, produces ={ "application/json" })
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(httpMethod = "POST", value = "Registers a customer to the application", notes = "Used as self-served operation",response = AuthenticationResponse.class)
    @ResponseBody
    public ResponseEntity<?> register(@Valid @RequestBody PersistableCustomer customer, HttpServletRequest request, HttpServletResponse response) throws Exception {

        
        
        //try {
            
            MerchantStore merchantStore = storeFacade.getByCode(request);
            Language language = languageUtils.getRESTLanguage(request);  
            
            //Transition
												System.out.println("$#11636#"); customer.setUserName(customer.getEmailAddress());
            
												System.out.println("$#11637#"); Validate.notNull(customer.getUserName(),"Username cannot be null");
												System.out.println("$#11638#"); Validate.notNull(customer.getBilling(),"Requires customer Country code");
												System.out.println("$#11639#"); Validate.notNull(customer.getBilling().getCountry(),"Requires customer Country code");
            
            customerFacade.registerCustomer(customer, merchantStore, language);
            
            // Perform the security
            Authentication authentication = null;
            try {
                
                authentication = jwtCustomerAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                customer.getUserName(),
                                customer.getPassword()
                        )
                );
                
            } catch(Exception e) {
																System.out.println("$#11640#"); return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
												System.out.println("$#11641#"); if(authentication == null) {
																System.out.println("$#11642#"); return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

												System.out.println("$#11643#"); SecurityContextHolder.getContext().setAuthentication(authentication);

            // Reload password post-security so we can generate token
            final JWTUser userDetails = (JWTUser)jwtCustomerDetailsService.loadUserByUsername(customer.getUserName());
            final String token = jwtTokenUtil.generateToken(userDetails);

            // Return the token
												System.out.println("$#11644#"); return ResponseEntity.ok(new AuthenticationResponse(customer.getId(),token));

        
    }

    /**
     * Authenticate a customer using username & password
     * @param authenticationRequest
     * @param device
     * @return
     * @throws AuthenticationException
     */
    @RequestMapping(value = "/customer/login", method = RequestMethod.POST, produces ={ "application/json" })
    @ApiOperation(httpMethod = "POST", value = "Authenticates a customer to the application", notes = "Customer can authenticate after registration, request is {\"username\":\"admin\",\"password\":\"password\"}",response = ResponseEntity.class)
    @ResponseBody
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) throws AuthenticationException {

    	//TODO SET STORE in flow
        // Perform the security
        Authentication authentication = null;
        try {
            
    
                //to be used when username and password are set
                authentication = jwtCustomerAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authenticationRequest.getUsername(),
                                authenticationRequest.getPassword()
                        )
                );

        } catch(BadCredentialsException unn) {
									System.out.println("$#11645#"); return new ResponseEntity<>("{\"message\":\"Bad credentials\"}",HttpStatus.UNAUTHORIZED);
        } catch(Exception e) {
												System.out.println("$#11646#"); return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
								System.out.println("$#11647#"); if(authentication == null) {
												System.out.println("$#11648#"); return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

								System.out.println("$#11649#"); SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        // todo create one for social
        final JWTUser userDetails = (JWTUser)jwtCustomerDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Return the token
								System.out.println("$#11650#"); return ResponseEntity.ok(new AuthenticationResponse(userDetails.getId(),token));
    }

    @RequestMapping(value = "/auth/customer/refresh", method = RequestMethod.GET, produces ={ "application/json" })
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);

								System.out.println("$#11651#"); System.out.println("--------------------- TOKEN : " + token);

        String username = jwtTokenUtil.getUsernameFromToken(token);
        JWTUser user = (JWTUser) jwtCustomerDetailsService.loadUserByUsername(username);

								System.out.println("$#11652#"); if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
												System.out.println("$#11653#"); return ResponseEntity.ok(new AuthenticationResponse(user.getId(),refreshedToken));
        } else {
												System.out.println("$#11654#"); return ResponseEntity.badRequest().body(null);
        }
    }
    

    @RequestMapping(value = "/customer/password/reset", method = RequestMethod.PUT, produces ={ "application/json" })
    @ApiOperation(httpMethod = "POST", value = "Change customer password", notes = "Change password request object is {\"username\":\"test@email.com\"}",response = ResponseEntity.class)
    public ResponseEntity<?> resetPassword(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletRequest request) {


        try {
            
            MerchantStore merchantStore = storeFacade.getByCode(request);
            Language language = languageUtils.getRESTLanguage(request);
            
            Customer customer = customerFacade.getCustomerByUserName(authenticationRequest.getUsername(), merchantStore);
            
												System.out.println("$#11655#"); if(customer == null){
																System.out.println("$#11656#"); return ResponseEntity.notFound().build();
            }
            
												System.out.println("$#11657#"); customerFacade.resetPassword(customer, merchantStore, language);
												System.out.println("$#11658#"); return ResponseEntity.ok(Void.class);
            
        } catch(Exception e) {
												System.out.println("$#11659#"); return ResponseEntity.badRequest().body("Exception when reseting password "+e.getMessage());
        }
    }
    

    @RequestMapping(value = "/customer/password", method = RequestMethod.POST, produces ={ "application/json" })
    @ApiOperation(httpMethod = "PUT", value = "Sends a request to reset password", notes = "Password reset request is {\"username\":\"test@email.com\"}",response = ResponseEntity.class)
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordRequest passwordRequest, HttpServletRequest request) {


        try {
            
            MerchantStore merchantStore = storeFacade.getByCode(request);

            Customer customer = customerFacade.getCustomerByUserName(passwordRequest.getUsername(), merchantStore);
            
												System.out.println("$#11660#"); if(customer == null){
																System.out.println("$#11661#"); return ResponseEntity.notFound().build();
            }
            
            //need to validate if password matches
												System.out.println("$#11662#"); if(!customerFacade.passwordMatch(passwordRequest.getCurrent(), customer)) {
              throw new ResourceNotFoundException("Username or password does not match");
            }
            
												System.out.println("$#11663#"); if(!passwordRequest.getPassword().equals(passwordRequest.getRepeatPassword())) {
              throw new ResourceNotFoundException("Both passwords do not match");
            }
            
												System.out.println("$#11664#"); customerFacade.changePassword(customer, passwordRequest.getPassword());
												System.out.println("$#11665#"); return ResponseEntity.ok(Void.class);
            
        } catch(Exception e) {
												System.out.println("$#11666#"); return ResponseEntity.badRequest().body("Exception when reseting password "+e.getMessage());
        }
    }
}
