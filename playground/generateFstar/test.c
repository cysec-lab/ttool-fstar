#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>
#include <limits.h>

#include "Add.h"

static unsigned int total = 0;
static unsigned int pass = 0;

void Testing_test_start(C_String_t title)
{
    {
        printf("  \x1b[36m[%s]\x1b[0m\n", title);
    }
}

void Testing_test_end()
{
    if (total != pass)
    {
        printf("  \x1b[35mSOME TESTS FAILED (%u/%u) (PASS/TOTAL)\x1b[0m\n", pass, total);
        exit(1);
    }
    else
    {
        puts("  \x1b[32mALL TESTS PASSED\x1b[0m\n");
    }
}

void test_static(bool is_pass)
{
    total++;
    if (total == UINT_MAX)
    {
        puts("test-code has so many tests.");
        exit(1);
    }

    if (is_pass == true)
    {
        pass++;
    }
}

#define MK_CHECK(n)                                                                                                         \
    void Testing_eq_i##n(C_String_t title, int##n##_t expect, int##n##_t result)                                            \
    {                                                                                                                       \
        bool is_pass = (expect == result);                                                                                  \
        test_static(is_pass);                                                                                               \
        if (is_pass)                                                                                                        \
        {                                                                                                                   \
        }                                                                                                                   \
        else                                                                                                                \
        {                                                                                                                   \
            printf("\x1b[31m✘\x1b[0m %s\n\t expected is %" PRId##n " but result is %" PRId##n "\n", title, expect, result); \
        }                                                                                                                   \
    }
MK_CHECK(8)
MK_CHECK(16)
MK_CHECK(32)
MK_CHECK(64)

#define MK_UCHECK(n)                                                                                                        \
    void Testing_eq_u##n(C_String_t title, uint##n##_t expect, uint##n##_t result)                                          \
    {                                                                                                                       \
        bool is_pass = (expect == result);                                                                                  \
        test_static(is_pass);                                                                                               \
        if (is_pass)                                                                                                        \
        {                                                                                                                   \
        }                                                                                                                   \
        else                                                                                                                \
        {                                                                                                                   \
            printf("\x1b[31m✘\x1b[0m %s\n\t expected is %" PRIu##n " but result is %" PRIu##n "\n", title, expect, result); \
        }                                                                                                                   \
    }
MK_UCHECK(8)
MK_UCHECK(16)
MK_UCHECK(32)
MK_UCHECK(64)

void Testing_eq_bool(C_String_t title, bool expect, bool result)
{
    bool is_pass = (expect == result);
    test_static(is_pass);
    if (is_pass)
    {
    }
    else
    {
        printf("\x1b[31m✘\x1b[0m %s\n\t expected is true but result is false\n", title);
    }
}

void Testing_eq_str(C_String_t title, C_String_t expect, C_String_t result)
{
    bool is_pass = (strcmp(expect, result) == 0);
    test_static(is_pass);
    if (is_pass)
    {
    }
    else
    {
        printf("\x1b[31m✘\x1b[0m %s\n\t expected is \'%s\' but result is \'%s\'\n", title, expect, result);
    }
}

void Testing_eq_u8_array(C_String_t title, C_String_t expect, uint32_t expect_length, C_String_t result)
{
    for (uint32_t i = 0; i < expect_length; i++)
    {
        if (expect[i] != result[i])
        {
            printf("\x1b[31m✘\x1b[0m %s\n\t expected is \'%s\' but result is '", title, expect);
            for (uint32_t i = 0; i <= expect_length; i++)
            {
                printf("%c", result[i] & 0x000000FF);
            }
            puts("'");
            test_static(false);
            return;
        }
    }

    test_static(true);
    return;
}

int main(int argc, char *argv[])
{
    kremlinit_globals();
    Testing_test_start("Add Test");
    Testing_eq_i32("Add", add(1, 2).value, 3);
    Testing_test_end();

    return 0;
}