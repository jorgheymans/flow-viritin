package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Payload;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.VBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Route
public class RecordFormView extends VerticalLayout {

    @BigShouldBeBigger
    record Person(@NotNull String name, @Min(0) Integer small, @Max(100) Integer big) {
    }

    public RecordFormView() {

        var record = new Person("Jorma", 70, 69);

        PersonForm form = new PersonForm();
        var binder = new VBinder<>(Person.class, form);
        binder.setValue(record);

        add(form);

        add(new Button("Show value", e -> {
            Notification.show("Value now:" + binder.getValue());
        }));

        add(new Button("Show validation errors", e -> {
            var constraintViolations = validate(binder.getValue());
            binder.setConstraintViolations(constraintViolations);
            Notification.show(constraintViolations.toString());
        }));

    }

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Constraint(validatedBy = { BigShouldBeBiggerValidator.class })
    public @interface BigShouldBeBigger {

        String message() default "Big should be bigger!";
    }


    public static class BigShouldBeBiggerValidator
            implements ConstraintValidator<BigShouldBeBigger, Person> {

        @Override
        public boolean isValid(Person car, ConstraintValidatorContext context) {
            if ( car == null ) {
                return true;
            }
            return car.big() > car.small();
        }
    }

    public static class PersonForm extends VerticalLayout {

        TextField name = new VTextField("Name");
        IntegerField small = new IntegerField("Small number");
        IntegerField big = new IntegerField("Big number");

        public PersonForm() {
            add(name,small,big);
        }
    }


    private static Set<ConstraintViolation<Person>> validate(Person record) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(record);
        return constraintViolations;
    }

}
