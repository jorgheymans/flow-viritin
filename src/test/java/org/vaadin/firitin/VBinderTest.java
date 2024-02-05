package org.vaadin.firitin;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.components.textfield.VIntegerField;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.VBinder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class VBinderTest {


    record FooBar(String foo, @Future LocalDateTime bar, Integer baz){}


    public static class FooBarForm extends VerticalLayout {
        TextField foo = new VTextField();
        DateTimePicker bar = new DateTimePicker();
        IntegerField baz = new VIntegerField();
    }

    @Test
    public void testRecordBasics() {

        FooBarForm fooBarForm = new FooBarForm();

        VBinder<FooBar> binder = new VBinder<>(FooBar.class, fooBarForm);

        FooBar value = binder.getValue();

        Assertions.assertEquals("", value.foo());
        // Vaadin Time Picker loses milliseconds...
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        FooBar jorma = new FooBar("Jorma", now, 69);
        binder.setValue(jorma);

        // Should not be the same reference as records are immutable
        Assertions.assertNotSame(jorma, binder.getValue());

        // But should be equal
        Assertions.assertEquals(jorma, binder.getValue());

        fooBarForm.foo.setValue("Kalle");
        LocalDateTime tomorrow = now.plusDays(1);
        fooBarForm.bar.setValue(tomorrow);
        fooBarForm.baz.setValue(70);

        FooBar kalle = binder.getValue();
        Assertions.assertEquals("Kalle", kalle.foo());
        Assertions.assertEquals(tomorrow, kalle.bar());
        Assertions.assertEquals(70, kalle.baz());


        fooBarForm.bar.setValue(LocalDateTime.now().minusDays(1));

        FooBar invalid = binder.getValue();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<FooBar>> violations = validator.validate(invalid);

        // TODO create a more trivial API for those not using BV API
        binder.setConstraintViolations(violations);

        Assertions.assertTrue(fooBarForm.bar.isInvalid());

    }
}
