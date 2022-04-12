package ru.inbox.vinnikov.tsys_sbb_railway_tickets.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SeleniumCheckLanguageAuth {
    public WebDriver driver;

    public SeleniumCheckLanguageAuth(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver; }

    @FindBy(xpath = "/html/body/section/div[1]/p/a/img")
    private WebElement flagDe;
    @FindBy(xpath = "/html/body/section/div[2]/p/a/img")
    private WebElement flagRu;
    @FindBy(xpath = "/html/body/section/div[3]/p/a/img")
    private WebElement flagUk;

    /**
     * методы для нажатия на флаги
     */
    public void clickFlagDe() {
        flagDe.click();
    }
    public void clickFlagRu() {
        flagRu.click();
    }
    public void clickFlagUk() {
        flagUk.click();
    }

    /**
     * метод для ввода логина
     */
//    public void inputLogin(String login) {
//        loginField.sendKeys(login); }
    /**
     * метод для ввода пароля
     */
//    public void inputPasswd(String passwd) {
//        passwdField.sendKeys(passwd); }
    /**
     * метод для осуществления нажатия кнопки входа в аккаунт
     */



}
