package com.mauriciotogneri.jsonschema;

import com.mauriciotogneri.jsonschema.annotations.AdditionalItems;
import com.mauriciotogneri.jsonschema.annotations.AdditionalProperties;
import com.mauriciotogneri.jsonschema.annotations.AllOf;
import com.mauriciotogneri.jsonschema.annotations.AnyOf;
import com.mauriciotogneri.jsonschema.annotations.Default;
import com.mauriciotogneri.jsonschema.annotations.Description;
import com.mauriciotogneri.jsonschema.annotations.ExclusiveMaximum;
import com.mauriciotogneri.jsonschema.annotations.ExclusiveMinimum;
import com.mauriciotogneri.jsonschema.annotations.Format;
import com.mauriciotogneri.jsonschema.annotations.MaxItems;
import com.mauriciotogneri.jsonschema.annotations.MaxLength;
import com.mauriciotogneri.jsonschema.annotations.MaxProperties;
import com.mauriciotogneri.jsonschema.annotations.Maximum;
import com.mauriciotogneri.jsonschema.annotations.MinItems;
import com.mauriciotogneri.jsonschema.annotations.MinLength;
import com.mauriciotogneri.jsonschema.annotations.MinProperties;
import com.mauriciotogneri.jsonschema.annotations.Minimum;
import com.mauriciotogneri.jsonschema.annotations.MultipleOf;
import com.mauriciotogneri.jsonschema.annotations.Name;
import com.mauriciotogneri.jsonschema.annotations.Not;
import com.mauriciotogneri.jsonschema.annotations.OneOf;
import com.mauriciotogneri.jsonschema.annotations.Optional;
import com.mauriciotogneri.jsonschema.annotations.Pattern;
import com.mauriciotogneri.jsonschema.annotations.Title;
import com.mauriciotogneri.jsonschema.annotations.UniqueItems;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Annotations
{
    private final Annotation[] annotations;

    public Annotations(Field field)
    {
        this.annotations = field.getAnnotations();
    }

    public Annotations(Class<?> clazz)
    {
        this.annotations = clazz.getAnnotations();
    }

    public String name()
    {
        Name name = annotation(Name.class);

        return (name != null) ? name.value() : null;
    }

    public String title()
    {
        Title title = annotation(Title.class);

        return (title != null) ? title.value() : null;
    }

    public Boolean optional()
    {
        Optional optional = annotation(Optional.class);

        return (optional != null);
    }

    public String description()
    {
        Description description = annotation(Description.class);

        return (description != null) ? description.value() : null;
    }

    public String format()
    {
        Format format = annotation(Format.class);

        return (format != null) ? format.value() : null;
    }

    public String pattern()
    {
        Pattern pattern = annotation(Pattern.class);

        return (pattern != null) ? pattern.value() : null;
    }

    public Integer minimum()
    {
        Minimum minimum = annotation(Minimum.class);

        return (minimum != null) ? minimum.value() : null;
    }

    public Integer maximum()
    {
        Maximum maximum = annotation(Maximum.class);

        return (maximum != null) ? maximum.value() : null;
    }

    public Float multipleOf()
    {
        MultipleOf multipleOf = annotation(MultipleOf.class);

        return (multipleOf != null) ? multipleOf.value() : null;
    }

    public Boolean exclusiveMinimum()
    {
        ExclusiveMinimum exclusiveMinimum = annotation(ExclusiveMinimum.class);

        return (exclusiveMinimum != null) ? exclusiveMinimum.value() : null;
    }

    public Boolean exclusiveMaximum()
    {
        ExclusiveMaximum exclusiveMaximum = annotation(ExclusiveMaximum.class);

        return (exclusiveMaximum != null) ? exclusiveMaximum.value() : null;
    }

    public Boolean uniqueItems()
    {
        UniqueItems uniqueItems = annotation(UniqueItems.class);

        return (uniqueItems != null) ? uniqueItems.value() : null;
    }

    public Boolean additionalItems()
    {
        AdditionalItems additionalItems = annotation(AdditionalItems.class);

        return (additionalItems != null) ? additionalItems.value() : null;
    }

    public Boolean additionalProperties()
    {
        AdditionalProperties additionalProperties = annotation(AdditionalProperties.class);

        return (additionalProperties != null) ? additionalProperties.value() : null;
    }

    public Integer minProperties()
    {
        MinProperties minProperties = annotation(MinProperties.class);

        return (minProperties != null) ? minProperties.value() : null;
    }

    public Integer maxProperties()
    {
        MaxProperties maxProperties = annotation(MaxProperties.class);

        return (maxProperties != null) ? maxProperties.value() : null;
    }

    public Integer minLength()
    {
        MinLength minLength = annotation(MinLength.class);

        return (minLength != null) ? minLength.value() : null;
    }

    public Integer maxLength()
    {
        MaxLength maxLength = annotation(MaxLength.class);

        return (maxLength != null) ? maxLength.value() : null;
    }

    public Integer minItems()
    {
        MinItems minItems = annotation(MinItems.class);

        return (minItems != null) ? minItems.value() : null;
    }

    public Integer maxItems()
    {
        MaxItems maxItems = annotation(MaxItems.class);

        return (maxItems != null) ? maxItems.value() : null;
    }

    public TypeDefinition[] allOf()
    {
        AllOf allOf = annotation(AllOf.class);

        return (allOf != null) ? TypeDefinition.fromList(allOf.value()) : null;
    }

    public TypeDefinition[] anyOf()
    {
        AnyOf anyOf = annotation(AnyOf.class);

        return (anyOf != null) ? TypeDefinition.fromList(anyOf.value()) : null;
    }

    public TypeDefinition[] oneOf()
    {
        OneOf oneOf = annotation(OneOf.class);

        return (oneOf != null) ? TypeDefinition.fromList(oneOf.value()) : null;
    }

    public TypeDefinition not()
    {
        Not not = annotation(Not.class);

        return (not != null) ? new TypeDefinition(not.value()) : null;
    }

    public String defaultValue()
    {
        Default defaultValue = annotation(Default.class);

        return (defaultValue != null) ? defaultValue.value() : null;
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A annotation(Class<A> type)
    {
        for (Annotation annotation : annotations)
        {
            if (annotation.annotationType().equals(type))
            {
                return (A) annotation;
            }
        }

        return null;
    }
}