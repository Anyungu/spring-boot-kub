package com.chef.ingredients.controllers;

import java.time.LocalDateTime;
import java.util.List;

import com.chef.ingredients.models.Ingredients;
import com.chef.ingredients.requests.IngredientsRequest;
import com.chef.ingredients.responses.GeneralResponse;
import com.chef.ingredients.responses.StatusResponse;
import com.chef.ingredients.services.IngredientsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/ingredients/v1")
public class IngredientsController {

    @Autowired
    private IngredientsService ingredientsService;

    @PostMapping(path = "/create")
    public StatusResponse createIngredient(@RequestBody IngredientsRequest ingredientsRequest) throws Exception {

        ingredientsService.createIngredient(ingredientsRequest);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "Created", 200);
        return stat;
    }

    @PutMapping(path = "/update")
    public StatusResponse updateIngredient(@RequestBody Ingredients ingredients) throws Exception {

        ingredientsService.updateIngredient(ingredients);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "updated", 200);
        return stat;
    }

    @DeleteMapping(path = "/delete")
    public StatusResponse deleteIngredient(Integer id) throws Exception {

        ingredientsService.deleteIngredient(id);

        StatusResponse stat = new StatusResponse(LocalDateTime.now().plusHours(3), "deleted", 200);
        return stat;
    }

    @GetMapping(path = "/read")
    public GeneralResponse<Ingredients> readIngredient(Integer id) throws Exception {

        Ingredients ingredient = ingredientsService.getIngredient(id);

        GeneralResponse<Ingredients> gen = new GeneralResponse<Ingredients>(LocalDateTime.now().plusHours(3), "Found",
                200, ingredient);
        return gen;
    }

    @GetMapping(path = "/readAll")
    public GeneralResponse<List<Ingredients>> readAllIngredient() throws Exception {

        List<Ingredients> ingredient = ingredientsService.getAllIngredient();

        GeneralResponse<List<Ingredients>> gen = new GeneralResponse<List<Ingredients>>(
                LocalDateTime.now().plusHours(3), "Found", 200, ingredient);
        return gen;
    }

}