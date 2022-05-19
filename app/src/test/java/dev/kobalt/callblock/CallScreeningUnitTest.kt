package dev.kobalt.callblock

import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.kobalt.callblock.contact.ContactRepository
import dev.kobalt.callblock.rule.RuleDao
import dev.kobalt.callblock.rule.RuleEntity
import dev.kobalt.callblock.rule.RuleRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test
import java.util.*

/** Unit tests for testing call screening actions. */
class CallScreeningUnitTest {

    /** Alternate instance of phone number utilities that don't require context. */
    private val utils = PhoneNumberUtil.getInstance()

    /** Returns normalized phone number value. */
    private fun String.normalizePhoneNumber(): String? {
        return utils.parse(this, Locale.getDefault().country).let {
            utils.format(it, PhoneNumberUtil.PhoneNumberFormat.E164)
        }
    }

    /** List of predefined rules. */
    private val predefinedList = listOf(
        RuleEntity(null, "4259501212".normalizePhoneNumber()!!, RuleEntity.Action.Warn, false),
        RuleEntity(null, "2539501212".normalizePhoneNumber()!!, RuleEntity.Action.Block, false)
    )

    /** List of contacts. */
    private val contactList = listOf(
        "4255551212".normalizePhoneNumber()!!,
        "2539501212".normalizePhoneNumber()!!,
        "2535551212".normalizePhoneNumber()!!
    )

    /** List of user rules. */
    private val userList = listOf(
        RuleEntity(null, "4259501212".normalizePhoneNumber()!!, RuleEntity.Action.Block, true),
        RuleEntity(null, "4255551212".normalizePhoneNumber()!!, RuleEntity.Action.Warn, true)
    )

    /** Mock of contact repository that contains mocked contacts. */
    private val mockedContactRepository = mockk<ContactRepository> {
        every { isNumberInContacts(any()) } returns false
        contactList.forEach {
            every { isNumberInContacts(it) } returns true
        }
    }

    /** Mock of rule DAO that returns mocked predefined and user rules.*/
    private val mockedDao = mockk<RuleDao> {
        every { getItemPredefinedOnlyByNumber(any()) } returns null
        predefinedList.forEach {
            every { getItemPredefinedOnlyByNumber(it.number!!) } returns it
        }
        every { getItemUserOnlyByNumber(any()) } returns null
        userList.forEach {
            every { getItemUserOnlyByNumber(it.number!!) } returns it
        }
    }

    @Test
            /** Validates proper action for given phone numbers using mocked set of rules. */
    fun validateGettingActionForPhoneNumber() {
        // List of phone numbers and their expected result.
        val numberExpectedList = listOf(
            "4259501212".normalizePhoneNumber()!! to RuleEntity.Action.Block, // Blocked by user defined rule.
            "2539501212".normalizePhoneNumber()!! to RuleEntity.Action.Block, // Blocked by predefined rule.
            "4255551212".normalizePhoneNumber()!! to RuleEntity.Action.Warn, // Warned by user defined rule.
            "2535551212".normalizePhoneNumber()!! to RuleEntity.Action.Allow // Allowed by contact defined rule.
        )
        // Rule repository that will use method getItemActionForPhoneNumber to verify states.
        val repository = spyk<RuleRepository> {
            every { preferencesRepository } returns mockk {
                every { useContactRules } returns true
                every { usePredefinedRules } returns true
                every { useUserRules } returns true
            }
            every { contactRepository } returns mockedContactRepository
            every { dao } returns mockedDao
        }
        // Process all numbers and give a pair of given result and expected result.
        val results =
            numberExpectedList.map { repository.getItemActionForPhoneNumber(it.first) to it.second }
        // Make sure all methods passed through.
        verify {
            numberExpectedList.forEach { repository.getItemActionForPhoneNumber(it.first) }
        }
        // To pass test, expected and given results must match.
        assert(results.all { it.first == it.second })
    }

    @Test
            /** Validates proper action for given phone numbers using mocked contacts only. */
    fun validateGettingActionForPhoneNumberWithContactsOnly() {
        // List of phone numbers and their expected result.
        val numberExpectedList = listOf(
            "4259501212".normalizePhoneNumber()!! to RuleEntity.Action.Block, // Blocked by contact defined rule.
            "2539501212".normalizePhoneNumber()!! to RuleEntity.Action.Allow, // Allowed by contact defined rule.
            "4255551212".normalizePhoneNumber()!! to RuleEntity.Action.Allow, // Allowed by contact defined rule.
            "2535551212".normalizePhoneNumber()!! to RuleEntity.Action.Allow // Allowed by contact defined rule.
        )
        // Rule repository that will use method getItemActionForPhoneNumber to verify states.
        val repository = spyk<RuleRepository> {
            every { preferencesRepository } returns mockk {
                every { useContactRules } returns true
                every { usePredefinedRules } returns false
                every { useUserRules } returns false
            }
            every { contactRepository } returns mockedContactRepository
            every { dao } returns mockedDao
        }
        // Process all numbers and give a pair of given result and expected result.
        val results =
            numberExpectedList.map { repository.getItemActionForPhoneNumber(it.first) to it.second }
        // Make sure all methods passed through.
        verify {
            numberExpectedList.forEach { repository.getItemActionForPhoneNumber(it.first) }
        }
        // To pass test, expected and given results must match.
        assert(results.all { it.first == it.second })
    }

}