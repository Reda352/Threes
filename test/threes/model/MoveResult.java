/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package threes.model;

/**
 *
 * @author Titouan HETMANIAK
 */
public class MoveResult {
    
    public final boolean moved;      // au moins une tuile a bougé ou fusionné
    public final int merges;         // nb fusions (optionnel)
    public MoveResult(boolean moved, int merges) {
        this.moved = moved;
        this.merges = merges;
    }
}

    

