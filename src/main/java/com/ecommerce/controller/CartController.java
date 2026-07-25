package com.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;

@Controller
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam int productId) {

        int userId = 1;

        Cart cart = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(1);
        }

        cartRepository.save(cart);

        return "redirect:/";
    }

    @GetMapping("/cart")
    public String cart(Model model) {

        List<Cart> carts = cartRepository.findByUserId(1);
        List<CartItem> cartItems = new ArrayList<>();

        for (Cart cart : carts) {

            Product product = productRepository.findById(cart.getProductId()).orElse(null);

            if (product != null) {

                CartItem item = new CartItem();

                item.setCartId(cart.getId());
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setImage(product.getImage());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());

                cartItems.add(item);
            }
        }

        double grandTotal = 0;

        for (CartItem item : cartItems) {
            grandTotal += item.getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);

        return "cart";
    }

    @GetMapping("/cart/remove")
    public String removeItem(@RequestParam int id) {

        cartRepository.deleteById(id);

        return "redirect:/cart";
    }

    @GetMapping("/cart/increase")
    public String increase(@RequestParam int id) {

        Cart cart = cartRepository.findById(id).orElse(null);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
            cartRepository.save(cart);
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart/decrease")
    public String decrease(@RequestParam int id) {

        Cart cart = cartRepository.findById(id).orElse(null);

        if (cart != null) {

            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
                cartRepository.save(cart);
            } else {
                cartRepository.delete(cart);
            }
        }

        return "redirect:/cart";
    }
}