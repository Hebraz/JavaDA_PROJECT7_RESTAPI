package com.nnk.springboot.domain.validation;

import com.nnk.springboot.domain.User;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.Oneway;
import javax.validation.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class PasswordConstraintValidatorTest extends TestCase {

    private static Validator validator;

    @Before
    public void initializeTest(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void isValidOK(){
        User user = new User("pierre.paul@","Az12345{","Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void isValidNotValidTooShort(){
        User user = new User("pierre.paul@","Az1234{","Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        ConstraintViolation<User>  constraintViolation = constraintViolations.stream().findFirst().get();
        Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "password");
        Assert.assertTrue(constraintViolation.getMessage().contains("TOO_SHORT"));
    }

    @Test
    public void isValidNotValidTooLong(){
        String stringOf126Chars = String.join("", Collections.nCopies(12, "Az1234{2ko")); //120
        stringOf126Chars = stringOf126Chars + "123456"; //126
        User user = new User("pierre.paul@",stringOf126Chars,"Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        ConstraintViolation<User>  constraintViolation = constraintViolations.stream().findFirst().get();
        Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "password");
        Assert.assertTrue(constraintViolation.getMessage().contains("TOO_LONG"));
    }

    @Test
    public void isValidNotValidNoDecimalDigit(){
        User user = new User("pierre.paul@","AzPaaaaao{","Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        ConstraintViolation<User>  constraintViolation = constraintViolations.stream().findFirst().get();
        Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "password");
        Assert.assertTrue(constraintViolation.getMessage().contains("INSUFFICIENT_DIGIT"));
    }

    @Test
    public void isValidNotValidNoSpecialChars(){
        User user = new User("pierre.paul@","Az123456","Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        ConstraintViolation<User>  constraintViolation = constraintViolations.stream().findFirst().get();
        Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "password");
        Assert.assertTrue(constraintViolation.getMessage().contains("INSUFFICIENT_SPECIAL"));
    }

    @Test
    public void isValidNotValidNoUppercase(){
        User user = new User("pierre.paul@","az21aaaaao{","Pierre paul","USER");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        ConstraintViolation<User>  constraintViolation = constraintViolations.stream().findFirst().get();
        Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "password");
        Assert.assertTrue(constraintViolation.getMessage().contains("INSUFFICIENT_UPPERCASE"));
    }
}