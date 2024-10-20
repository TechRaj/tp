package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_CONSTRAINTS_LENGTH;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDCON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.DelMedConCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MedCon;
import seedu.address.model.person.Nric;

/**
 * Parses user input for the {@link DelMedConCommand} and creates a new instance of it.
 */
public class DelMedConCommandParser implements Parser<DelMedConCommand> {

    private static final Logger logger = LogsCenter.getLogger(DelMedConCommandParser.class);

    /**
     * Parses the given arguments string and creates a {@link DelMedConCommand} object.
     *
     * @param args the arguments string containing user input.
     * @return A {@link DelMedConCommand} object containing the parsed NRIC and set of medical conditions.
     * @throws ParseException if the user input does not conform to the expected format or
     *         if the NRIC is not provided.
     */
    public DelMedConCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NRIC, PREFIX_MEDCON);

        // Check if NRIC is provided
        if (!arePrefixesPresent(argMultimap, PREFIX_NRIC, PREFIX_MEDCON) || !argMultimap.getPreamble().isEmpty()) {
            logger.warning("NRIC not provided in DelMedConCommand arguments.");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DelMedConCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NRIC);

        // Parse NRIC
        String nricStr = argMultimap.getValue(PREFIX_NRIC).get();
        Nric nric = ParserUtil.parseNric(nricStr);

        // Parse all MedCon values and add them to a set
        Set<MedCon> medCons = new HashSet<>();
        for (String medConStr : argMultimap.getAllValues(PREFIX_MEDCON)) {
            if (medConStr.isEmpty()) {
                logger.warning("Medical condition is empty.");
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DelMedConCommand.MESSAGE_USAGE));
            } else if (medConStr.length() > 45) {
                logger.warning("Medical condition exceeds character limit: " + medConStr);
                throw new ParseException(MESSAGE_CONSTRAINTS_LENGTH);
            } else {
                medCons.add(new MedCon(medConStr));
            }
        }

        return new DelMedConCommand(nric, medCons);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
