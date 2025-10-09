package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;

public interface AuthService {
    
    User authenticate(String email, String password);
    // Or return boolean or token
    
}

//Define methods for the contract.
// At this stage, a typical first method would be authenticate(String email, String password).
//The method can return a boolean, a User object, or (eventually)
// a token string depending on how you plan to implement authentication.
//Add any necessary imports for models or exceptions you may use.