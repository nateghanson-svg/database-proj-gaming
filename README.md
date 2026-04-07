# database-proj-gaming
My final project for CS-1103 focusing on different video games across all platforms.
Proposal: 
This will likely focus on only around 20 games from each platform, including smoe on multiple platforms.
Each game will have a title, release year, company, and copies sold column. 
The primary key will be game_id - an exclusive number given to each game.
The foreign keys will be:
company_id in tables Companies and Games (many-to-one)
game_id in tables Game_Platform and Games (many-to-one)
platform_id in tables Game_Platform and Platforms (many-to-one)
This creates a many-to-many relationship between tables Games and Platforms.
