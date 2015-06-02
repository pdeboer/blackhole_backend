-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 02. Jun 2015 um 12:37
-- Server-Version: 5.6.24
-- PHP-Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `pplibdataanalyzer`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `coordinates`
--

CREATE TABLE IF NOT EXISTS `coordinates` (
  `Id` int(11) NOT NULL,
  `ra` varchar(255) NOT NULL,
  `dec` varchar(255) NOT NULL,
  `active` smallint(1) unsigned NOT NULL DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `coordinates`
--

INSERT INTO `coordinates` (`Id`, `ra`, `dec`, `active`) VALUES
(1, '173.34776259000532', '55.07240556666321', 1),
(2, '158.60454273', '45.50473245', 1),
(3, '173.72062567', '44.00484757', 1),
(4, '151.67687946', '51.27331502', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tasklog`
--

CREATE TABLE IF NOT EXISTS `tasklog` (
  `id` int(11) NOT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `question` varchar(45) DEFAULT NULL,
  `answer` varchar(45) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `tasklog`
--

INSERT INTO `tasklog` (`id`, `uuid`, `question`, `answer`) VALUES
(1, '1', '1', 'yes'),
(8, '1', '2', 'yes'),
(9, '1', '3', 'yes');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tasks`
--

CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int(11) NOT NULL,
  `task` varchar(255) DEFAULT NULL,
  `taskType` set('Boolean','Freetext','Random') DEFAULT NULL,
  `value` int(11) DEFAULT '1',
  `formerTaskId` int(11) DEFAULT NULL,
  `laterTaskId` int(11) DEFAULT NULL,
  `exitOn` set('Yes','No','Freetext') DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `tasks`
--

INSERT INTO `tasks` (`id`, `task`, `taskType`, `value`, `formerTaskId`, `laterTaskId`, `exitOn`, `comment`) VALUES
(1, 'Is there any structure?', 'Boolean', 1, 0, 2, 'No', 'First Task to be solved'),
(2, 'Is it a clumpy structure?', 'Boolean', 1, 1, 3, '', 'Second Task'),
(3, 'Check X-Ray; Has it data?', 'Boolean', 1, 2, 4, 'No', 'X Ray'),
(4, 'If so, do I see an object?', 'Boolean', 1, 3, 0, '', 'X Ray'),
(5, 'Check Spectrum on SDSS; Do we have a spectrum there', 'Boolean', 1, 2, 6, 'No', 'SDSS'),
(6, 'If so, is the spectrum of Point Source broad', 'Boolean', 1, 5, 0, '', 'SDSS'),
(7, 'Check Radio; Do we have radio of the object', 'Boolean', 1, 2, 8, 'No', 'Radio'),
(8, 'Is there a strong source at the position of the candidate Point Source', 'Boolean', 1, 7, 0, '', 'Radio');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `Id` int(11) NOT NULL,
  `email` varchar(255) CHARACTER SET latin1 NOT NULL,
  `firstname` varchar(255) CHARACTER SET latin1 NOT NULL,
  `lastname` varchar(255) CHARACTER SET latin1 NOT NULL,
  `roleId` smallint(1) NOT NULL DEFAULT '3',
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `users`
--

INSERT INTO `users` (`Id`, `email`, `firstname`, `lastname`, `roleId`, `password`) VALUES
(1, 'david.pinezich@gmail.com', 'David', 'Pinezich', 3, 'test'),
(2, 'david@gmail.com', 'David ', 'Tester', 3, 'test1'),
(3, 'hans@mosers.ch', 'Hans', 'Moser', 3, 'test2'),
(4, 'pdeboer@ifi.uzh.ch', 'Patrick', 'de Boer', 3, 'patrick');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `coordinates`
--
ALTER TABLE `coordinates`
  ADD PRIMARY KEY (`Id`);

--
-- Indizes für die Tabelle `tasklog`
--
ALTER TABLE `tasklog`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`Id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `coordinates`
--
ALTER TABLE `coordinates`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT für Tabelle `tasklog`
--
ALTER TABLE `tasklog`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT für Tabelle `users`
--
ALTER TABLE `users`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
