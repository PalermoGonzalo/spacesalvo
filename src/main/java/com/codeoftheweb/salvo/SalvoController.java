import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class SalvoController {
    @RequestMapping("/greeting")
    public String greeting(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}
