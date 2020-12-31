package com;

public abstract class ClientServerErrorCodes {
    // ERROR CODES
        // login
        private static final int LOGIN_OK                   = 100;
        private static final int USERNAME_NOT_PRESENT       = 101;
        private static final int USERNAME_EMPTY             = 102;
        private static final int PSW_INCORRECT              = 103;
        private static final int LOGOUT_OK                  = 105;

        public static int LOGIN_OK()                    { return LOGIN_OK; }
        public static int USERNAME_NOT_PRESENT()        { return USERNAME_NOT_PRESENT; }
        public static int USERNAME_EMPTY()              { return USERNAME_EMPTY; }
        public static int PSW_INCORRECT()               { return PSW_INCORRECT; }
        public static int LOGOUT_OK()                   { return LOGOUT_OK; }

        // registration
        private static final int REGISTRATION_OK            = 110;
        private static final int USERNAME_ALREADY_PRESENT   = 111;

        public static int REGISTRATION_OK()             { return REGISTRATION_OK; }
        public static int USERNAME_ALREADY_PRESENT()    { return USERNAME_ALREADY_PRESENT; }

        public static void printError(int code) {
            switch(code) {
                case LOGIN_OK:
                    System.out.println(LOGIN_OK + " LOGIN_OK");
                    break;
                case USERNAME_NOT_PRESENT:
                    System.out.println(USERNAME_NOT_PRESENT + " USERNAME_NOT_PRESENT");
                    break;
                case USERNAME_EMPTY:
                    System.out.println(USERNAME_EMPTY + " USERNAME_EMPTY");
                    break;
                case PSW_INCORRECT:
                    System.out.println(PSW_INCORRECT + " PSW_INCORRECT");
                    break;
                case LOGOUT_OK:
                    System.out.println(LOGOUT_OK + " LOGOUT_OK");
                    break;
                case REGISTRATION_OK:
                    System.out.println(REGISTRATION_OK + " REGISTRATION_OK");
                    break;
                case USERNAME_ALREADY_PRESENT:
                    System.out.println(USERNAME_ALREADY_PRESENT + " USERNAME_ALREADY_PRESENT");
                    break;

                default:
                    System.out.println("Questo codice di ritorno non è valido");
            }
        }
}
