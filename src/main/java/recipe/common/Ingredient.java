package recipe.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("slug")
    private String slug;
    
    @JsonProperty("amount")
    private double amount;
    
    @JsonProperty("unit")
    private String unit;
    
    @JsonProperty("isPrime")
    private boolean isPrime;
    
    @JsonProperty("scalingFactor")
    private double scalingFactor;
    
    // Default constructor for Jackson
    public Ingredient() {
        this.scalingFactor = 1.0;
    }
    
    public Ingredient(String name, double amount, String unit) {
        this.name = SlugUtils.normalizeDisplayName(name);
        this.slug = SlugUtils.toSlug(name);
        this.amount = amount;
        this.unit = unit;
        this.isPrime = false;
        this.scalingFactor = 1.0;
    }
    
    public Ingredient(String name, double amount, String unit, boolean isPrime) {
        this(name, amount, unit);
        this.isPrime = isPrime;
    }
    
    public Ingredient(String name, double amount, String unit, boolean isPrime, double scalingFactor) {
        this(name, amount, unit, isPrime);
        this.scalingFactor = scalingFactor;
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
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public boolean isPrime() {
        return isPrime;
    }
    
    public void setPrime(boolean prime) {
        isPrime = prime;
    }
    
    public double getScalingFactor() {
        return scalingFactor;
    }
    
    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
    
    /**
     * Scales this ingredient by a given factor
     */
    public Ingredient scaled(double scaleFactor) {
        return new Ingredient(
            this.name,
            this.amount * scaleFactor * this.scalingFactor,
            this.unit,
            this.isPrime,
            this.scalingFactor
        );
    }
    
    @Override
    public String toString() {
        return String.format("%.2f %s %s%s", 
            amount, unit, name, isPrime ? " (prime)" : "");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Ingredient ingredient = (Ingredient) obj;
        return slug != null ? slug.equals(ingredient.slug) : ingredient.slug == null;
    }
    
    @Override
    public int hashCode() {
        return slug != null ? slug.hashCode() : 0;
    }
}