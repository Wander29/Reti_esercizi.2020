package com.esempioIO;

import java.io.*;
public class TestDataIOStream {
    public static void main(String[] args) {
        String filename = "data-out.dat";
        String message  = "Hi,$%&ááùü!";
// Write primitives to an output file
        try (DataOutputStream out =
                 new DataOutputStream(
                     new BufferedOutputStream(
                         new FileOutputStream(filename))))
        {
            out.writeByte(127);
            out.writeShort(-1);
            out.writeInt(43981);
            out.writeLong(305419896);
            out.writeFloat(11.22f);
            out.writeDouble(55.66);
            out.writeBoolean(true);
            out.writeBoolean(false);

            for (int i = 0; i < message.length(); ++i) {
                out.writeChar(message.charAt(i)); }
            out.writeChars(message);
            out.writeBytes(message);
            out.flush();
        } catch (IOException ex) { ex.printStackTrace(); }

        // Read raw bytes and print in Hex
        try (BufferedInputStream in =
                 new BufferedInputStream(
                         new FileInputStream(filename)))
        {
            int inByte;
            while ((inByte = in.read()) != -1) {
                System.out.printf("%02X ", inByte);
                System.out.println();// Print Hex codes
            }
            System.out.printf("%n%n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Read primitives
        try (DataInputStream in =
                     new DataInputStream(
                             new BufferedInputStream(
                                     new FileInputStream(filename))))
        {
            System.out.println("byte:" + in.readByte());
            System.out.println("short: " + in.readShort());
            System.out.println("int: " + in.readInt());
            System.out.println("long: " + in.readLong());
            System.out.println("float: " + in.readFloat());
            System.out.println("double: " + in.readDouble());
            System.out.println("boolean: " + in.readBoolean());
            System.out.println("boolean: " + in.readBoolean());

            System.out.print("char: ");
            for (int i = 0; i < message.length(); ++i) {
                System.out.print(in.readChar()); }

            System.out.println();
            System.out.print("chars: ");
            for (int i = 0; i < message.length(); ++i) {
                System.out.print(in.readChar()); }

            System.out.println();
            System.out.println();
            /*
            ATTENZIONE! readByte non traduce i bytes da formato di scrittura a locale!
                possibili errori
             */
            System.out.print("bytes: ");
            for (int i = 0; i < message.length(); ++i) {
                System.out.print((char)in.readByte()); }
            System.out.println();
        }
        catch (IOException ex) { ex.printStackTrace(); }
    }
}
