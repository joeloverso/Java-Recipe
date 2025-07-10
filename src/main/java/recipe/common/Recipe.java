package recipe.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Recipe {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("slug")
    private String slug;
    
    @JsonProperty("servings")
    private double servings;
    
    @JsonProperty("ingredients")
    private List<Ingredient> ingredients;
    
    @JsonProperty("instructions")
    private List<String> instructions;
    
    @JsonProperty("created")
    private String created;
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    
    // Default constructor for Jackson
    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.created = Instant.now().toString();
    }
    
    public Recipe(String name, double servings) {
        this.name = SlugUtils.normalizeDisplayName(name);
        this.slug = SlugUtils.toSlug(name);
        this.servings = servings;
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.created = Instant.now().toString();
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = SlugUtils.normalizeDisplayName(name);
        this.slug = SlugUtils.toSlug(name);
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public double getServings() {
        return servings;
    }
    
    public void setServings(double servings) {
        this.servings = servings;
    }
    
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    
    public List<String> getInstructions() {
        return instructions;
    }
    
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
    
    public String getCreated() {
        return created;
    }
    
    public void setCreated(String created) {
        this.created = created;
    }
    
    // Utility methods
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }
    
    public void addInstruction(String instruction) {
        this.instructions.add(instruction.trim());
    }
    
    @com.fasterxml.jackson.annotation.JsonIgnore
    public List<Ingredient> getPrimeIngredients() {
        return ingredients.stream()
            .filter(Ingredient::isPrime)
            .collect(Collectors.toList());
    }
    
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean hasPrimeIngredients() {
        return ingredients.stream().anyMatch(Ingredient::isPrime);
    }
    
    /**
     * Creates a scaled version of this recipe
     */
    public Recipe scaled(double scaleFactor) {
        Recipe scaledRecipe = new Recipe(this.name + " (scaled)", (int) Math.ceil(this.servings * scaleFactor));
        
        // Scale ingredients
        for (Ingredient ingredient : this.ingredients) {
            scaledRecipe.addIngredient(ingredient.scaled(scaleFactor));
        }
        
        // Copy instructions (no scaling needed)
        scaledRecipe.setInstructions(new ArrayList<>(this.instructions));
        
        return scaledRecipe;
    }
    
    /**
     * Validates that the recipe has required fields
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               servings > 0 &&
               !ingredients.isEmpty() &&
               !instructions.isEmpty();
    }
    
    /**
     * Serializes this recipe to JSON string
     */
    public String toJson() throws IOException {
        return objectMapper.writeValueAsString(this);
    }
    
    /**
     * Deserializes a recipe from JSON string
     */
    public static Recipe fromJson(String json) throws IOException {
        return objectMapper.readValue(json, Recipe.class);
    }
    
    /**
     * Loads a recipe from a JSON file
     */
    public static Recipe fromFile(File file) throws IOException {
        return objectMapper.readValue(file, Recipe.class);
    }
    
    /**
     * Saves this recipe to a JSON file
     */
    public void saveToFile(File file) throws IOException {
        objectMapper.writeValue(file, this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe: ").append(name).append("\n");
        sb.append("Servings: ").append(servings).append("\n");
        sb.append("Ingredients:\n");
        for (Ingredient ingredient : ingredients) {
            sb.append("  - ").append(ingredient.toString()).append("\n");
        }
        sb.append("Instructions:\n");
        for (int i = 0; i < instructions.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(instructions.get(i)).append("\n");
        }
        return sb.toString();
    }
}