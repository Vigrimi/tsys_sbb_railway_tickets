package ru.inbox.vinnikov.tsys_sbb_railway_tickets.selenium;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static java.lang.Thread.sleep;

//@SpringBootTest
public class SeleniumLoginTest {
    public static SeleniumCheckLanguageAuth checkLanguageAuth;
    public static WebDriver driver;

    // аннотацией Junit «@BeforeClass», которая указывает на то, что метод будет выполняться один раз до выполнения
    // всех тестов в классе. Тестовые методы в Junit помечаются аннотацией Test.
    @BeforeClass
    public static void setup() throws InterruptedException {
        //определение пути до драйвера и его настройка
        System.setProperty("webdriver.chrome.driver", SeleniumConfProperties.getProperty("chromedriver"));
        //создание экземпляра драйвера
        driver = new ChromeDriver();
        checkLanguageAuth = new SeleniumCheckLanguageAuth(driver);
        //окно разворачивается на полный экран driver.manage().window().maximize();
        driver.manage().window().setSize(new Dimension(1350,550));
        //задержка на выполнение теста = 10 сек.
        sleep(5_000);
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //получение ссылки на страницу входа из файла настроек
        driver.get(SeleniumConfProperties.getProperty("loginpage"));
    }

    @Test
    public void flagAuthDe() throws InterruptedException {
        checkLanguageAuth.clickFlagDe();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement textInDeutsch = driver.findElement(By.xpath("/html/body/a"));
        String textInDeutschStr = textInDeutsch.getText();
//        System.out.println("-----------test textInDeutschStr:" + textInDeutschStr);
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        //и сравниваем его с нужным
        Assert.assertEquals("Klicken um anmelden!", textInDeutschStr);
    }
    @Test
    public void flagAuthRu() throws InterruptedException {
        WebElement backSbbLogo = driver.findElement(By.xpath("/html/body/p/a/img"));
        backSbbLogo.click();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        checkLanguageAuth.clickFlagRu();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement textInRu = driver.findElement(By.xpath("/html/body/a"));
        String textInRuStr = textInRu.getText();
//        System.out.println("-----------test textInRuStr:" + textInRuStr);
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        //и сравниваем его с нужным
        Assert.assertEquals("Зарегистрироваться!", textInRuStr);
    }
    @Test
    public void flagAuthUk() throws InterruptedException {
        WebElement backSbbLogo = driver.findElement(By.xpath("/html/body/p/a/img"));
        backSbbLogo.click();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        checkLanguageAuth.clickFlagUk();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement textInUk = driver.findElement(By.xpath("/html/body/a"));
        String textInUkStr = textInUk.getText();
//        System.out.println("-----------test textInUkStr:" + textInUkStr);
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        //и сравниваем его с нужным
        Assert.assertEquals("Click to Sign-up!", textInUkStr);
    }
    @Test
    public void loginUserRu() throws InterruptedException {
        WebElement backSbbLogo = driver.findElement(By.xpath("/html/body/p/a/img"));
        backSbbLogo.click();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        checkLanguageAuth.clickFlagRu();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement loginField = driver.findElement(By.xpath("/html/body/form/input[2]"));
        loginField.sendKeys(SeleniumConfProperties.getProperty("testuserlogin"));
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement passwordField = driver.findElement(By.xpath("/html/body/form/input[3]"));
        passwordField.sendKeys(SeleniumConfProperties.getProperty("testuserpassword"));
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement loginSubmitBtn = driver.findElement(By.xpath("/html/body/form/input[4]"));
        loginSubmitBtn.click();
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        WebElement loginSuccesful = driver.findElement(By.xpath("/html/body/font/b/p"));
        String loginSuccesfulStr = loginSuccesful.getText();
//        System.out.println("-----------test textInRuStr:" + loginSuccesfulStr);
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        //и сравниваем его с нужным
        Assert.assertEquals("Добро пожаловать в личный кабинет пользователя - клиент компании USER!"
                , loginSuccesfulStr);
    }
    @Test
    public void loginUserUk() throws InterruptedException {
        driver.get("http://localhost:8090/sbb/v1/logout");
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        String currentUrl = driver.getCurrentUrl();
//        System.out.println("-----------test currentUrl:" + currentUrl);
        //задержка на выполнение теста, чтобы пользователь видел результат
        sleep(2_000);
        //и сравниваем его с нужным
        Assert.assertEquals(SeleniumConfProperties.getProperty("loginpage"), currentUrl);
    }

    @AfterClass
    public static void teardown() {
        driver.quit();
    }
}
