package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
    @GetMapping("/create")
    public String showCreateForm() {
        return "create";  // Resolves to /templates/create.html
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error, 
                                @RequestParam(required = false) String logout, 
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("logout", "You have been logged out");
        }
        return "login";  // Resolves to /templates/login.html
    }
    
    @GetMapping("/home")
    public String showHome(Principal principal, Model model) {
    	if (principal != null) {
            model.addAttribute("username", principal.getName());
        } else {
            model.addAttribute("username", "Guest");
        }
        return "home";
    }
    
    /*
     @GetMapping("/home")
public ResponseEntity<String> home(Principal principal) {
    return ResponseEntity.ok("Welcome, " + principal.getName() + "! You are logged in.");
}
     */
}