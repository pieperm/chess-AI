// The traditional 8x8 chess board with pieces.

// DO NOT MODIFY THIS FILE
// Never try to directly create an instance of this class, or modify its member variables.
// Instead, you should only be reading its variables and calling its functions.

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
// <<-- Creer-Merge: usings -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
// you can add additional using(s) here
// <<-- /Creer-Merge: usings -->>

/// <summary>
/// The traditional 8x8 chess board with pieces.
/// </summary>
namespace Joueur.cs.Games.Chess
{
    /// <summary>
    /// The traditional 8x8 chess board with pieces.
    /// </summary>
    public class Game : BaseGame
    {
        /// <summary>
        /// The game version hash, used to compare if we are playing the same version on the server.
        /// </summary>
        new protected static string GameVersion = "cfa5f5c1685087ce2899229c04c26e39f231e897ecc8fe036b44bc22103ef801";

        #region Properties
        /// <summary>
        /// Forsyth-Edwards Notation (fen), a notation that describes the game board state.
        /// </summary>
        public string Fen { get; protected set; }

        /// <summary>
        /// The list of [known] moves that have occurred in the game, in Universal Chess Inferface (UCI) format. The first element is the first move, with the last element being the most recent.
        /// </summary>
        public IList<string> History { get; protected set; }

        /// <summary>
        /// List of all the players in the game.
        /// </summary>
        public IList<Chess.Player> Players { get; protected set; }

        /// <summary>
        /// A unique identifier for the game instance that is being played.
        /// </summary>
        public string Session { get; protected set; }


        // <<-- Creer-Merge: properties -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        // you can add additional properties(s) here. None of them will be tracked or updated by the server.
        // <<-- /Creer-Merge: properties -->>
        #endregion


        #region Methods
        /// <summary>
        /// Creates a new instance of Game. Used during game initialization, do not call directly.
        /// </summary>
        protected Game() : base()
        {
            this.Name = "Chess";

            this.History = new List<string>();
            this.Players = new List<Chess.Player>();
        }


        // <<-- Creer-Merge: methods -->> - Code you add between this comment and the end comment will be preserved between Creer re-runs.
        // you can add additional method(s) here.

        /// <summary>
        /// Print Chess board to console.
        /// </summary>
        public void Print() 
        {
			string FEN = Fen;
			char I_File='a';
			int I_Rank=8;
			int Wid_Hgh=8;

			for(int i=0; i<Wid_Hgh+1; i++) 
			{
				Console.Write("+---");
			}
			Console.Write("+\n|  ");
			for(int i=0; i<Wid_Hgh; i++) 
			{
				Console.Write(" | "+(char)(I_File+i)); 
			} 
			Console.Write(" |\n");
			for(int i=0; i<Wid_Hgh+1; i++) 
			{
				Console.Write("+---");
			}
			Console.Write("+\n");
			Console.Write("| "+I_Rank);

			int Cnt=1;
			foreach (char c in FEN)
			{
				if(Char.IsLetter(c)) 
				{
					Console.Write(" | "+c);
				} 
				else if(Char.IsDigit(c)) 
				{
					for(char k=c; k>'0'; k--) 
					{
						Console.Write(" |  ");
					}
				} 
				else if(c=='/') 
				{
					Console.Write(" |\n"); 
					for(int i=0; i<Wid_Hgh+1; i++) 
					{
						Console.Write("+---");
					}
					Console.Write("+\n");
					Console.Write("| "+(I_Rank-Cnt++));
				} 
				else if(c==' ') 
				{
					break;
				}
			}
			Console.Write(" |\n");
			for(int i=0; i<Wid_Hgh+1; i++) 
			{
				Console.Write("+---");
			}
			Console.Write("+\n");

        }

        // <<-- /Creer-Merge: methods -->>
        #endregion
    }
}
