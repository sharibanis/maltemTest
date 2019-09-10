public static String encrypt(String message, int shift) {

        StringBuilder output = new StringBuilder("");
        for(char letter : message.toCharArray())
        {
          if(letter > 96 && letter < 123)//Lower case letter
            {
                char encrypted = (char) (letter+shift);
                if(encrypted > 122)
                {
                    encrypted = (char) ((encrypted % 122) + 96);
                }
                output.append(encrypted);
            } 
            else if(letter > 47 && letter < 58)// new shift
            {
                output.append(letter);
                shift = shift + (letter - 48);
            } 
            else//Symbol
            {
                output.append(letter);
            }
        }
        return output.toString();


    }