package com.chef.ingredients.services;

import java.util.List;
import java.util.Optional;

import com.chef.ingredients.exceptions.CustomException;
import com.chef.ingredients.models.Ingredients;
import com.chef.ingredients.repos.IngredientsRepos;
import com.chef.ingredients.requests.IngredientsRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientsService {

    @Autowired
    private IngredientsRepos ingredientsRepos;

    public void createIngredient(IngredientsRequest ingredientsRequest) throws Exception {

        Ingredients ing = new Ingredients();
        ing.setEmail(ingredientsRequest.getEmail());
        ing.setName(ingredientsRequest.getName());

        ingredientsRepos.save(ing);

    }

    public void updateIngredient(Ingredients ingredients) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(ingredients.getId());

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        Ingredients ingredientsNew = findById.get();
        ingredientsNew.setEmail(ingredients.getEmail());
        ingredientsNew.setName(ingredients.getName());

        ingredientsRepos.save(ingredientsNew);

    }

    public void deleteIngredient(Integer id) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(id);

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        ingredientsRepos.deleteById(id);

    }

    public Ingredients getIngredient(Integer id) throws Exception {

        Optional<Ingredients> findById = ingredientsRepos.findById(id);

        if (!findById.isPresent()) {
            throw new CustomException(401, "Not Found");
        }

        return findById.get();
    }

    public List<Ingredients> getAllIngredient() throws Exception {

        List<Ingredients> findAll = ingredientsRepos.findAll();

        if (findAll.isEmpty()) {
            throw new CustomException(401, "Not Found");
        }

        return findAll;
    }

}
