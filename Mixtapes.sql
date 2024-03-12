-- script for queries
CREATE TABLE IF NOT EXISTS Mixtapes_Users (
	uid INT NOT NULL auto_increment,
    username VARCHAR(255) NOT NULL,
    passcode VARCHAR(255) NOT NULL,
    auth_token VARCHAR(255) NOT NULL,
    PRIMARY KEY (uid)
);

CREATE TABLE Mixtapes_Playlists(
	pid INTEGER AUTO_INCREMENT,
	uid INTEGER NOT NULL,
	pName VARCHAR(45),
	link VARCHAR(100),
	isPublic BOOL,
	PRIMARY KEY(pid),
	FOREIGN KEY (uid) REFERENCES Mixtapes_Users(uid)
);

CREATE TABLE Mixtapes_Entries(
	pid INTEGER NOT NULL,
	sid INTEGER NOT NULL,
	PRIMARY KEY(pid, sid),
	FOREIGN KEY (pid) REFERENCES Mixtapes_Playlists(pid),
	FOREIGN KEY (sid) REFERENCES Mixtapes_Songs(sid)
);

CREATE TABLE Mixtapes_Songs(
	sid INTEGER AUTO_INCREMENT,
	title VARCHAR(50),
	artist VARCHAR(50),
	album VARCHAR(50),
	duration VARCHAR(10),
	genre VARCHAR(20),
	PRIMARY KEY(sid)
);

CREATE TABLE Mixtapes_Collaborators(
	pid INTEGER NOT NULL,
	uid INTEGER NOT NULL,
	PRIMARY KEY(pid, uid),
	FOREIGN KEY (pid) REFERENCES Mixtapes_Playlists(pid),
	FOREIGN KEY (uid) REFERENCES Mixtapes_Users(uid)
);
