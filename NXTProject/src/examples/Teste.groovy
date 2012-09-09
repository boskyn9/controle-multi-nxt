/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package examples


(0..100).each{    
    java.awt.Color c = new java.awt.Color(new Random().nextInt(100000000))
    println c.toString()
}
